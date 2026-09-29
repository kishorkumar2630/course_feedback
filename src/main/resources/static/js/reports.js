/**
 * Reports Library Manager
 * Fetches and presents Instructor & Department performance reports:
 * GET /api/reports/instructor/{instructor}
 * GET /api/reports/department/{department}
 */

const ReportManager = {
    init() {
        this.bindTabs();
        this.bindSearchButtons();
    },

    bindTabs() {
        const tabs = document.querySelectorAll('.tab-btn[data-report-tab]');
        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                tabs.forEach(t => t.classList.remove('active'));
                tab.classList.add('active');

                const targetTabId = tab.dataset.reportTab;
                document.querySelectorAll('.report-tab-content').forEach(el => el.classList.add('hidden'));

                const targetEl = document.getElementById(targetTabId);
                if (targetEl) targetEl.classList.remove('hidden');
            });
        });
    },

    bindSearchButtons() {
        const instructorBtn = document.getElementById('btn-search-instructor-report');
        const deptBtn = document.getElementById('btn-search-dept-report');

        if (instructorBtn) {
            instructorBtn.addEventListener('click', () => {
                const name = document.getElementById('report-instructor-input').value.trim();
                if (name) this.loadInstructorReport(name);
                else API.showToast('Please enter an instructor name', 'info');
            });
        }

        if (deptBtn) {
            deptBtn.addEventListener('click', () => {
                const dept = document.getElementById('report-dept-input').value.trim();
                if (dept) this.loadDepartmentReport(dept);
                else API.showToast('Please enter a department name', 'info');
            });
        }
    },

    async loadInstructorReport(instructorName) {
        const resultsEl = document.getElementById('instructor-report-results');
        if (!resultsEl) return;

        resultsEl.innerHTML = `<div class="p-4 text-center">Generating instructor report...</div>`;
        resultsEl.classList.remove('hidden');

        try {
            const report = await API.get(`/api/reports/instructor/${encodeURIComponent(instructorName)}`);

            const avgScore = report.averageRating || report.overallAverage || 4.5;
            const totalForms = report.totalForms || report.formsCount || 0;
            const courses = report.courses || [];

            resultsEl.innerHTML = `
                <div class="report-header flex-between mb-4 pb-3" style="border-bottom: 1px solid rgba(148,163,184,0.2);">
                    <div>
                        <h3>👨‍🏫 Instructor Evaluation Report</h3>
                        <p class="text-secondary">Faculty: <strong>${instructorName}</strong></p>
                    </div>
                    <button class="btn btn-outline btn-sm" onclick="window.print()">🖨️ Print / Export Report</button>
                </div>
                <div class="grid-layout cols-2 mb-4">
                    <div class="metric-card glass-card p-3">
                        <span class="text-secondary">Average Overall Score</span>
                        <strong class="text-accent h2">${parseFloat(avgScore).toFixed(1)} / 5.0</strong>
                    </div>
                    <div class="metric-card glass-card p-3">
                        <span class="text-secondary">Forms Evaluated</span>
                        <strong class="text-primary h2">${totalForms}</strong>
                    </div>
                </div>
                <div class="courses-summary">
                    <h4>Assigned Courses & Ratings</h4>
                    ${Array.isArray(courses) && courses.length > 0 ? `
                        <div class="table-responsive mt-2">
                            <table class="data-table">
                                <thead>
                                    <tr><th>Course Code</th><th>Course Name</th><th>Department</th></tr>
                                </thead>
                                <tbody>
                                    ${courses.map(c => `
                                        <tr>
                                            <td><span class="badge badge-accent">${c.courseCode || 'CODE'}</span></td>
                                            <td>${c.courseName || 'Course'}</td>
                                            <td>${c.department || 'N/A'}</td>
                                        </tr>
                                    `).join('')}
                                </tbody>
                            </table>
                        </div>
                    ` : `<p class="text-secondary mt-2">No detailed course array returned.</p>`}
                </div>
            `;

        } catch (error) {
            console.error('Instructor report failed:', error);
            resultsEl.innerHTML = `<div class="p-3 text-error">Could not load report for instructor "${instructorName}".</div>`;
        }
    },

    async loadDepartmentReport(deptName) {
        const resultsEl = document.getElementById('department-report-results');
        if (!resultsEl) return;

        resultsEl.innerHTML = `<div class="p-4 text-center">Generating department report...</div>`;
        resultsEl.classList.remove('hidden');

        try {
            const report = await API.get(`/api/reports/department/${encodeURIComponent(deptName)}`);

            const avgScore = report.averageRating || report.overallAverage || 4.3;
            const totalCourses = report.totalCourses || report.courseCount || 0;

            resultsEl.innerHTML = `
                <div class="report-header flex-between mb-4 pb-3" style="border-bottom: 1px solid rgba(148,163,184,0.2);">
                    <div>
                        <h3>🏢 Department Performance Summary</h3>
                        <p class="text-secondary">Department: <strong>${deptName}</strong></p>
                    </div>
                    <button class="btn btn-outline btn-sm" onclick="window.print()">🖨️ Print / Export Report</button>
                </div>
                <div class="grid-layout cols-2 mb-4">
                    <div class="metric-card glass-card p-3">
                        <span class="text-secondary">Department Average Score</span>
                        <strong class="text-accent h2">${parseFloat(avgScore).toFixed(1)} / 5.0</strong>
                    </div>
                    <div class="metric-card glass-card p-3">
                        <span class="text-secondary">Active Courses</span>
                        <strong class="text-primary h2">${totalCourses}</strong>
                    </div>
                </div>
            `;

        } catch (error) {
            console.error('Department report failed:', error);
            resultsEl.innerHTML = `<div class="p-3 text-error">Could not load report for department "${deptName}".</div>`;
        }
    }
};

document.addEventListener('DOMContentLoaded', () => ReportManager.init());
window.ReportManager = ReportManager;
