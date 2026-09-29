/**
 * Student Portal Logic
 * Loads active feedback forms, enrolled courses, and triggers feedback submission modal
 */

const StudentPortal = {
    async loadData() {
        const currentUser = AuthManager.getCurrentUser();
        if (!currentUser || currentUser.role !== 'student') return;

        const welcomeMsg = document.getElementById('student-welcome-msg');
        if (welcomeMsg) {
            welcomeMsg.textContent = `Welcome back, ${currentUser.name}! Submit feedback for your enrolled courses below.`;
        }

        await Promise.all([
            this.loadAvailableForms(),
            this.loadEnrolledCourses()
        ]);
    },

    async loadAvailableForms() {
        const formsList = document.getElementById('student-forms-list');
        const activeCount = document.getElementById('student-active-count');
        if (!formsList) return;

        try {
            const forms = await API.get('/api/feedback-forms/getAll');
            // Filter published / active forms
            const publishedForms = Array.isArray(forms) ? forms.filter(f => f.status === 'PUBLISHED' || f.status === 'ACTIVE') : [];

            if (activeCount) activeCount.textContent = `${publishedForms.length} Available`;

            if (publishedForms.length === 0) {
                formsList.innerHTML = `
                    <div class="empty-state p-4 text-center">
                        <div class="empty-icon" style="font-size: 2rem;">📭</div>
                        <p class="text-secondary mt-2">No active feedback forms are currently open for submission.</p>
                    </div>
                `;
                return;
            }

            formsList.innerHTML = publishedForms.map(form => `
                <div class="list-item-card glass-card p-3 mb-3">
                    <div class="card-top flex-between mb-2">
                        <span class="badge badge-accent">${form.course ? form.course.courseCode : 'COURSE'}</span>
                        <span class="text-secondary small">Closes: ${form.closingDate || 'N/A'}</span>
                    </div>
                    <h4 class="form-title mb-1">${form.course ? form.course.courseName : 'Course Evaluation'}</h4>
                    <p class="text-secondary small mb-3">
                        Semester: ${form.semester || 'Current'} | Instructor: ${form.course ? form.course.instructor : 'Faculty'}
                    </p>
                    <button class="btn btn-primary btn-sm w-full btn-take-feedback" data-form-id="${form.id}">
                        ✍️ Take Feedback
                    </button>
                </div>
            `).join('');

            // Bind click events on Take Feedback buttons
            formsList.querySelectorAll('.btn-take-feedback').forEach(btn => {
                btn.addEventListener('click', () => {
                    const formId = btn.dataset.formId;
                    if (window.FeedbackManager) {
                        window.FeedbackManager.openSubmissionModal(formId);
                    }
                });
            });

        } catch (error) {
            console.error('Failed to load student feedback forms:', error);
            formsList.innerHTML = `<div class="p-3 text-error">Failed to load feedback forms.</div>`;
        }
    },

    async loadEnrolledCourses() {
        const coursesList = document.getElementById('student-courses-list');
        const coursesCount = document.getElementById('student-courses-count');
        if (!coursesList) return;

        try {
            const courses = await API.get('/api/courses/getAll');
            const courseArray = Array.isArray(courses) ? courses : [];

            if (coursesCount) coursesCount.textContent = `${courseArray.length} Enrolled`;

            if (courseArray.length === 0) {
                coursesList.innerHTML = `
                    <div class="empty-state p-4 text-center">
                        <p class="text-secondary">No enrolled courses found.</p>
                    </div>
                `;
                return;
            }

            coursesList.innerHTML = courseArray.map(course => `
                <div class="list-item-card glass-card p-3 mb-3">
                    <div class="flex-between mb-1">
                        <strong>${course.courseCode}</strong>
                        <span class="badge badge-outline">${course.department || 'General'}</span>
                    </div>
                    <h4>${course.courseName}</h4>
                    <p class="text-secondary small">Instructor: ${course.instructor || 'Unassigned'} | Sem: ${course.semester || 'N/A'}</p>
                </div>
            `).join('');

        } catch (error) {
            console.error('Failed to load student courses:', error);
            coursesList.innerHTML = `<div class="p-3 text-error">Failed to load courses.</div>`;
        }
    }
};

window.StudentPortal = StudentPortal;
