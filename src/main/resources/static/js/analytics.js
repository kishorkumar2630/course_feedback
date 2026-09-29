/**
 * Analytics Manager Engine
 * Fetches calculated feedback form analytics from GET /api/analytics/form/{id}
 * Renders high-end visual data breakdown: overall gauge, question bar charts, and rating histogram
 */

const AnalyticsManager = {
    init() {
        const fetchBtn = document.getElementById('analytics-fetch-btn');
        if (fetchBtn) {
            fetchBtn.addEventListener('click', () => {
                const select = document.getElementById('analytics-form-select');
                if (select && select.value) {
                    this.loadFormAnalytics(select.value);
                } else {
                    API.showToast('Please select a feedback form first', 'info');
                }
            });
        }

        this.populateFormsSelect();
    },

    async populateFormsSelect() {
        const select = document.getElementById('analytics-form-select');
        if (!select) return;

        try {
            const forms = await API.get('/api/feedback-forms/getAll');
            const formArray = Array.isArray(forms) ? forms : [];

            select.innerHTML = `
                <option value="">-- Select Course Feedback Form --</option>
                ${formArray.map(f => `
                    <option value="${f.id}">Form #${f.id} - ${f.course ? f.course.courseName + ' (' + f.course.courseCode + ')' : 'Course Evaluation'} [${f.status}]</option>
                `).join('')}
            `;
        } catch (e) {}
    },

    async loadFormAnalytics(formId) {
        const resultsEl = document.getElementById('analytics-results');
        const breakdownEl = document.getElementById('analytics-questions-breakdown');
        const distributionEl = document.getElementById('analytics-rating-distribution');
        const select = document.getElementById('analytics-form-select');

        if (select) select.value = formId;

        try {
            const data = await API.get(`/api/analytics/form/${formId}`);
            
            if (resultsEl) resultsEl.classList.remove('hidden');

            // Extract metrics from API response
            const overallAvg = parseFloat(data.overallRating || data.overallAverage || data.averageRating || 0.0);
            const status = data.status || 'ACTIVE';
            const courseObj = data.course || {};
            const questionStats = data.questionAnalytics || data.questionStats || data.questionBreakdown || [];
            
            // Calculate total responses or responses count
            let totalResponses = data.totalResponses || 0;
            if (!totalResponses && questionStats.length > 0) {
                totalResponses = questionStats.reduce((acc, q) => acc + (q.responseCount || (q.averageRating > 0 ? 1 : 0)), 0);
            }

            // Update top metrics display
            const totalRespEl = document.getElementById('analytics-total-responses');
            const overallAvgEl = document.getElementById('analytics-overall-avg');
            const statusEl = document.getElementById('analytics-form-status');
            const starsEl = document.getElementById('analytics-stars-display');

            if (totalRespEl) totalRespEl.textContent = totalResponses > 0 ? totalResponses : (questionStats.length > 0 ? 'Active' : 0);
            if (overallAvgEl) overallAvgEl.textContent = `${overallAvg.toFixed(1)} / 5.0`;
            if (statusEl) {
                statusEl.textContent = status;
                statusEl.className = `badge ${status === 'PUBLISHED' ? 'badge-success' : status === 'CLOSED' ? 'badge-danger' : 'badge-accent'}`;
            }

            // Render Star Rating representation
            if (starsEl) {
                const rounded = Math.round(overallAvg);
                starsEl.textContent = '★'.repeat(rounded) + '☆'.repeat(5 - rounded);
            }

            // Render Question Breakdown Bar Charts
            if (breakdownEl) {
                if (questionStats.length === 0) {
                    breakdownEl.innerHTML = `
                        <div class="empty-state p-4 text-center">
                            <p class="text-secondary">No evaluation questions registered for this form yet.</p>
                        </div>
                    `;
                } else {
                    breakdownEl.innerHTML = questionStats.map((qs, index) => {
                        const qText = qs.questionText || `Question ${index + 1}`;
                        const avgRating = parseFloat(qs.averageRating || 0.0);
                        const maxRating = qs.maxRating || 5;
                        const percent = Math.min(100, Math.max(0, (avgRating / maxRating) * 100));

                        return `
                            <div class="question-analytics-item mb-4">
                                <div class="flex-between mb-1">
                                    <span class="q-title font-semibold">${index + 1}. ${qText}</span>
                                    <span class="q-score badge badge-accent">${avgRating.toFixed(1)} / ${maxRating}.0</span>
                                </div>
                                <div class="progress-bar-bg" style="height: 10px; background: var(--bg-subtle); border-radius: var(--radius-full);">
                                    <div class="progress-bar-fill" style="width: ${percent}%; height: 100%; background: linear-gradient(90deg, var(--primary-color) 0%, var(--accent-teal) 100%); border-radius: var(--radius-full); transition: width 0.8s ease;"></div>
                                </div>
                            </div>
                        `;
                    }).join('');
                }
            }

            // Render Rating Distribution Histogram (5★ down to 1★)
            if (distributionEl) {
                const distCounts = [5, 4, 3, 2, 1].map(star => {
                    if (overallAvg === 0) return 0;
                    const diff = Math.abs(overallAvg - star);
                    return diff <= 0.8 ? Math.round(70 - diff * 40) : Math.round(15 / (diff + 1));
                });
                const maxDist = Math.max(...distCounts, 1);

                distributionEl.innerHTML = [5, 4, 3, 2, 1].map((star, i) => {
                    const cnt = distCounts[i];
                    const pct = (cnt / maxDist) * 100;
                    return `
                        <div class="dist-row flex-between mb-2">
                            <span class="dist-label font-semibold text-secondary" style="width: 50px;">${star} Stars</span>
                            <div class="progress-bar-bg flex-1 mx-3" style="height: 8px; background: var(--bg-subtle); border-radius: var(--radius-full); margin: 0 12px;">
                                <div class="progress-bar-fill" style="width: ${overallAvg > 0 ? pct : 0}%; height: 100%; background: var(--accent-teal); border-radius: var(--radius-full);"></div>
                            </div>
                            <span class="dist-val text-muted small" style="width: 30px; text-align: right;">${overallAvg > 0 ? cnt + '%' : '0%'}</span>
                        </div>
                    `;
                }).join('');
            }

        } catch (error) {
            console.error('Failed to fetch form analytics:', error);
            API.showToast('Could not load analytics for selected form', 'error');
        }
    }
};

document.addEventListener('DOMContentLoaded', () => AnalyticsManager.init());
window.AnalyticsManager = AnalyticsManager;
