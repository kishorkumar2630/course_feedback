/**
 * Faculty Portal Logic
 * Displays faculty assigned courses, feedback forms, and links to live form analytics
 */

const FacultyPortal = {
    async loadData() {
        const currentUser = AuthManager.getCurrentUser();
        if (!currentUser || currentUser.role !== 'faculty') return;

        const welcomeMsg = document.getElementById('faculty-welcome-msg');
        if (welcomeMsg) {
            welcomeMsg.textContent = `Welcome, ${currentUser.name}! Review feedback forms and live performance analytics for your courses.`;
        }

        await this.loadFacultyForms();
    },

    async loadFacultyForms() {
        const container = document.getElementById('faculty-forms-list');
        if (!container) return;

        try {
            const forms = await API.get('/api/feedback-forms/getAll');
            const formArray = Array.isArray(forms) ? forms : [];

            if (formArray.length === 0) {
                container.innerHTML = `
                    <div class="empty-state p-4 text-center">
                        <p class="text-secondary">No feedback forms created yet.</p>
                    </div>
                `;
                return;
            }

            container.innerHTML = `
                <div class="grid-layout cols-2">
                    ${formArray.map(form => `
                        <div class="glass-card p-4">
                            <div class="flex-between mb-2">
                                <span class="badge badge-accent">${form.course ? form.course.courseCode : 'COURSE'}</span>
                                <span class="badge ${form.status === 'PUBLISHED' ? 'badge-success' : form.status === 'CLOSED' ? 'badge-danger' : 'badge-warning'}">
                                    ${form.status}
                                </span>
                            </div>
                            <h4 class="mb-1">${form.course ? form.course.courseName : 'Feedback Form #' + form.id}</h4>
                            <p class="text-secondary small mb-3">
                                Semester: ${form.semester || 'N/A'} | Closing Date: ${form.closingDate || 'N/A'}
                            </p>
                            <button class="btn btn-primary btn-sm w-full btn-view-analytics" data-form-id="${form.id}">
                                📊 View Live Analytics
                            </button>
                        </div>
                    `).join('')}
                </div>
            `;

            // Bind View Live Analytics buttons
            container.querySelectorAll('.btn-view-analytics').forEach(btn => {
                btn.addEventListener('click', () => {
                    const formId = btn.dataset.formId;
                    if (window.Navigation) {
                        window.Navigation.switchView('analytics-view');
                    }
                    if (window.AnalyticsManager) {
                        window.AnalyticsManager.loadFormAnalytics(formId);
                    }
                });
            });

        } catch (error) {
            console.error('Failed to load faculty feedback forms:', error);
            container.innerHTML = `<div class="p-3 text-error">Failed to load course forms.</div>`;
        }
    }
};

window.FacultyPortal = FacultyPortal;
