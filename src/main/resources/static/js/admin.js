/**
 * Admin Management Portal
 * Manages full CRUD workflows for Feedback Forms, Questions, Students, Courses, and Responses
 */

const AdminPortal = {
    activeTab: 'forms-tab',

    init() {
        this.bindTabs();
        this.bindActionButtons();
    },

    bindTabs() {
        const tabBtns = document.querySelectorAll('.tab-btn[data-admin-tab]');
        tabBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                const targetTabId = btn.dataset.adminTab;
                tabBtns.forEach(b => b.classList.remove('active'));
                btn.classList.add('active');

                // Hide all admin tab contents
                document.querySelectorAll('.admin-tab-content').forEach(tab => tab.classList.add('hidden'));
                
                const activeTabEl = document.getElementById(`admin-${targetTabId}`);
                if (activeTabEl) activeTabEl.classList.remove('hidden');

                this.activeTab = targetTabId;
                this.loadTabContent(targetTabId);
            });
        });
    },

    loadData() {
        this.loadTabContent(this.activeTab);
    },

    loadTabContent(tabId) {
        switch (tabId) {
            case 'forms-tab':
                this.loadForms();
                break;
            case 'questions-tab':
                this.loadQuestions();
                break;
            case 'students-tab':
                this.loadStudents();
                break;
            case 'courses-tab':
                this.loadCourses();
                break;
            case 'responses-tab':
                this.loadResponses();
                break;
        }
    },

    // -------------------------------------------------------------
    // 1. FEEDBACK FORMS MANAGEMENT
    // -------------------------------------------------------------
    async loadForms() {
        const tbody = document.getElementById('admin-forms-tbody');
        if (!tbody) return;

        try {
            const forms = await API.get('/api/feedback-forms/getAll');
            const formArray = Array.isArray(forms) ? forms : [];

            if (formArray.length === 0) {
                tbody.innerHTML = `<tr><td colspan="7" class="text-center text-secondary">No feedback forms found.</td></tr>`;
                return;
            }

            tbody.innerHTML = formArray.map(form => `
                <tr>
                    <td><strong>#${form.id}</strong></td>
                    <td>${form.course ? form.course.courseName : 'N/A'}</td>
                    <td><span class="badge badge-accent">${form.course ? form.course.courseCode : 'N/A'}</span></td>
                    <td>${form.semester || 'N/A'}</td>
                    <td>
                        <span class="badge ${form.status === 'PUBLISHED' ? 'badge-success' : form.status === 'CLOSED' ? 'badge-danger' : 'badge-warning'}">
                            ${form.status}
                        </span>
                    </td>
                    <td>${form.closingDate || 'N/A'}</td>
                    <td class="action-buttons">
                        ${form.status !== 'PUBLISHED' ? `<button class="btn btn-sm btn-success btn-publish-form" data-id="${form.id}">Publish</button>` : ''}
                        ${form.status !== 'CLOSED' ? `<button class="btn btn-sm btn-outline btn-close-form" data-id="${form.id}">Close</button>` : ''}
                        <button class="btn btn-sm btn-danger btn-delete-form" data-id="${form.id}">Delete</button>
                    </td>
                </tr>
            `).join('');

            // Bind Form actions
            tbody.querySelectorAll('.btn-publish-form').forEach(btn => {
                btn.addEventListener('click', async () => {
                    try {
                        await API.put(`/api/feedback-forms/publish/${btn.dataset.id}`);
                        API.showToast('Form published successfully!', 'success');
                        this.loadForms();
                    } catch (e) {}
                });
            });

            tbody.querySelectorAll('.btn-close-form').forEach(btn => {
                btn.addEventListener('click', async () => {
                    try {
                        await API.put(`/api/feedback-forms/close/${btn.dataset.id}`);
                        API.showToast('Form closed successfully!', 'info');
                        this.loadForms();
                    } catch (e) {}
                });
            });

            tbody.querySelectorAll('.btn-delete-form').forEach(btn => {
                btn.addEventListener('click', async () => {
                    if (confirm('Are you sure you want to delete this feedback form?')) {
                        try {
                            await API.delete(`/api/feedback-forms/delete/${btn.dataset.id}`);
                            API.showToast('Feedback form deleted!', 'success');
                            this.loadForms();
                        } catch (e) {}
                    }
                });
            });

        } catch (error) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center text-error">Failed to load forms.</td></tr>`;
        }
    },

    // -------------------------------------------------------------
    // 2. QUESTIONS MANAGEMENT
    // -------------------------------------------------------------
    async loadQuestions() {
        const tbody = document.getElementById('admin-questions-tbody');
        if (!tbody) return;

        try {
            const questions = await API.get('/api/questions/getAll');
            const qArray = Array.isArray(questions) ? questions : [];

            if (qArray.length === 0) {
                tbody.innerHTML = `<tr><td colspan="5" class="text-center text-secondary">No questions found.</td></tr>`;
                return;
            }

            tbody.innerHTML = qArray.map(q => `
                <tr>
                    <td><strong>#${q.id}</strong></td>
                    <td>Form #${q.feedbackForm ? q.feedbackForm.id : 'N/A'}</td>
                    <td>${q.questionText}</td>
                    <td><span class="badge badge-primary">${q.maxRating || 5} Stars</span></td>
                    <td class="action-buttons">
                        <button class="btn btn-sm btn-danger btn-delete-q" data-id="${q.id}">Delete</button>
                    </td>
                </tr>
            `).join('');

            tbody.querySelectorAll('.btn-delete-q').forEach(btn => {
                btn.addEventListener('click', async () => {
                    if (confirm('Delete this question?')) {
                        try {
                            await API.delete(`/api/questions/delete/${btn.dataset.id}`);
                            API.showToast('Question deleted!', 'success');
                            this.loadQuestions();
                        } catch (e) {}
                    }
                });
            });

        } catch (error) {
            tbody.innerHTML = `<tr><td colspan="5" class="text-center text-error">Failed to load questions.</td></tr>`;
        }
    },

    // -------------------------------------------------------------
    // 3. STUDENTS MANAGEMENT
    // -------------------------------------------------------------
    async loadStudents() {
        const tbody = document.getElementById('admin-students-tbody');
        if (!tbody) return;

        try {
            const students = await API.get('/api/students/getAll');
            const sArray = Array.isArray(students) ? students : [];

            if (sArray.length === 0) {
                tbody.innerHTML = `<tr><td colspan="4" class="text-center text-secondary">No students registered.</td></tr>`;
                return;
            }

            tbody.innerHTML = sArray.map(s => `
                <tr>
                    <td><strong>#${s.id}</strong></td>
                    <td><span class="badge badge-accent">${s.studentIdentifier}</span></td>
                    <td>${s.name}</td>
                    <td class="action-buttons">
                        <button class="btn btn-sm btn-danger btn-delete-student" data-id="${s.id}">Delete</button>
                    </td>
                </tr>
            `).join('');

            tbody.querySelectorAll('.btn-delete-student').forEach(btn => {
                btn.addEventListener('click', async () => {
                    if (confirm('Delete this student?')) {
                        try {
                            await API.delete(`/api/students/delete/${btn.dataset.id}`);
                            API.showToast('Student deleted!', 'success');
                            this.loadStudents();
                        } catch (e) {}
                    }
                });
            });

        } catch (error) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-center text-error">Failed to load students.</td></tr>`;
        }
    },

    // -------------------------------------------------------------
    // 4. COURSES MANAGEMENT
    // -------------------------------------------------------------
    async loadCourses() {
        const tbody = document.getElementById('admin-courses-tbody');
        if (!tbody) return;

        try {
            const courses = await API.get('/api/courses/getAll');
            const cArray = Array.isArray(courses) ? courses : [];

            if (cArray.length === 0) {
                tbody.innerHTML = `<tr><td colspan="7" class="text-center text-secondary">No courses found.</td></tr>`;
                return;
            }

            tbody.innerHTML = cArray.map(c => `
                <tr>
                    <td><strong>#${c.id}</strong></td>
                    <td><span class="badge badge-accent">${c.courseCode}</span></td>
                    <td>${c.courseName}</td>
                    <td>${c.instructor || 'Unassigned'}</td>
                    <td>${c.department || 'N/A'}</td>
                    <td>${c.semester || 'N/A'}</td>
                    <td class="action-buttons">
                        <button class="btn btn-sm btn-danger btn-delete-course" data-id="${c.id}">Delete</button>
                    </td>
                </tr>
            `).join('');

            tbody.querySelectorAll('.btn-delete-course').forEach(btn => {
                btn.addEventListener('click', async () => {
                    if (confirm('Delete this course?')) {
                        try {
                            await API.delete(`/api/courses/delete/${btn.dataset.id}`);
                            API.showToast('Course deleted!', 'success');
                            this.loadCourses();
                        } catch (e) {}
                    }
                });
            });

        } catch (error) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center text-error">Failed to load courses.</td></tr>`;
        }
    },

    // -------------------------------------------------------------
    // 5. RESPONSES MANAGEMENT
    // -------------------------------------------------------------
    async loadResponses() {
        const tbody = document.getElementById('admin-responses-tbody');
        if (!tbody) return;

        try {
            const responses = await API.get('/api/responses/getAll');
            const rArray = Array.isArray(responses) ? responses : [];

            if (rArray.length === 0) {
                tbody.innerHTML = `<tr><td colspan="5" class="text-center text-secondary">No submitted responses yet.</td></tr>`;
                return;
            }

            tbody.innerHTML = rArray.map(r => `
                <tr>
                    <td><strong>#${r.id}</strong></td>
                    <td>${r.student ? r.student.name : 'Anonymous'}</td>
                    <td>Form #${r.feedbackForm ? r.feedbackForm.id : 'N/A'}</td>
                    <td>${r.submittedAt ? new Date(r.submittedAt).toLocaleString() : 'N/A'}</td>
                    <td class="action-buttons">
                        <button class="btn btn-sm btn-danger btn-delete-response" data-id="${r.id}">Delete</button>
                    </td>
                </tr>
            `).join('');

            tbody.querySelectorAll('.btn-delete-response').forEach(btn => {
                btn.addEventListener('click', async () => {
                    if (confirm('Delete this response?')) {
                        try {
                            await API.delete(`/api/responses/delete/${btn.dataset.id}`);
                            API.showToast('Response deleted!', 'success');
                            this.loadResponses();
                        } catch (e) {}
                    }
                });
            });

        } catch (error) {
            tbody.innerHTML = `<tr><td colspan="5" class="text-center text-error">Failed to load responses.</td></tr>`;
        }
    },

    // -------------------------------------------------------------
    // MODAL FORM CREATION BINDINGS
    // -------------------------------------------------------------
    bindActionButtons() {
        document.getElementById('btn-create-form')?.addEventListener('click', () => this.openCreateFormModal());
        document.getElementById('btn-create-question')?.addEventListener('click', () => this.openCreateQuestionModal());
        document.getElementById('btn-create-student')?.addEventListener('click', () => this.openCreateStudentModal());
        document.getElementById('btn-create-course')?.addEventListener('click', () => this.openCreateCourseModal());

        const modalForm = document.getElementById('crud-modal-form');
        if (modalForm) {
            modalForm.addEventListener('submit', (e) => this.handleCrudSubmit(e));
        }
    },

    async openCreateFormModal() {
        try {
            const courses = await API.get('/api/courses/getAll');
            const fieldsEl = document.getElementById('crud-form-fields');
            document.getElementById('crud-modal-title').textContent = 'Create Feedback Form';
            modalFormTarget = 'form';

            fieldsEl.innerHTML = `
                <div class="form-group mb-3">
                    <label>Select Course</label>
                    <select id="field-course-id" class="form-input" required>
                        ${courses.map(c => `<option value="${c.id}">${c.courseCode} - ${c.courseName}</option>`).join('')}
                    </select>
                </div>
                <div class="form-group mb-3">
                    <label>Semester</label>
                    <input type="text" id="field-semester" class="form-input" placeholder="e.g. Fall 2026" value="Fall 2026" required>
                </div>
                <div class="form-group mb-3">
                    <label>Closing Date</label>
                    <input type="date" id="field-closing-date" class="form-input" value="${new Date(Date.now() + 14*86400000).toISOString().split('T')[0]}" required>
                </div>
            `;
            window.Navigation?.openModal('crud-modal');
        } catch (e) {
            API.showToast('Failed to prepare form creation modal', 'error');
        }
    },

    async openCreateQuestionModal() {
        try {
            const forms = await API.get('/api/feedback-forms/getAll');
            const fieldsEl = document.getElementById('crud-form-fields');
            document.getElementById('crud-modal-title').textContent = 'Add Question to Form';
            modalFormTarget = 'question';

            fieldsEl.innerHTML = `
                <div class="form-group mb-3">
                    <label>Select Feedback Form</label>
                    <select id="field-form-id" class="form-input" required>
                        ${forms.map(f => `<option value="${f.id}">Form #${f.id} - ${f.course ? f.course.courseName : 'Course'}</option>`).join('')}
                    </select>
                </div>
                <div class="form-group mb-3">
                    <label>Question Text</label>
                    <input type="text" id="field-question-text" class="form-input" placeholder="e.g. How effective was the course structure?" required>
                </div>
                <div class="form-group mb-3">
                    <label>Max Rating Scale</label>
                    <input type="number" id="field-max-rating" class="form-input" value="5" min="1" max="10" required>
                </div>
            `;
            window.Navigation?.openModal('crud-modal');
        } catch (e) {
            API.showToast('Failed to prepare question modal', 'error');
        }
    },

    openCreateStudentModal() {
        const fieldsEl = document.getElementById('crud-form-fields');
        document.getElementById('crud-modal-title').textContent = 'Register New Student';
        modalFormTarget = 'student';

        fieldsEl.innerHTML = `
            <div class="form-group mb-3">
                <label>Student Identifier / Reg No</label>
                <input type="text" id="field-student-id" class="form-input" placeholder="e.g. STU1005" required>
            </div>
            <div class="form-group mb-3">
                <label>Full Name</label>
                <input type="text" id="field-student-name" class="form-input" placeholder="e.g. Sarah Connor" required>
            </div>
        `;
        window.Navigation?.openModal('crud-modal');
    },

    openCreateCourseModal() {
        const fieldsEl = document.getElementById('crud-form-fields');
        document.getElementById('crud-modal-title').textContent = 'Add New Course';
        modalFormTarget = 'course';

        fieldsEl.innerHTML = `
            <div class="form-group mb-3">
                <label>Course Code</label>
                <input type="text" id="field-course-code" class="form-input" placeholder="e.g. CS101" required>
            </div>
            <div class="form-group mb-3">
                <label>Course Name</label>
                <input type="text" id="field-course-name" class="form-input" placeholder="e.g. Intro to Computer Science" required>
            </div>
            <div class="form-group mb-3">
                <label>Instructor</label>
                <input type="text" id="field-instructor" class="form-input" placeholder="e.g. Dr. John Doe" required>
            </div>
            <div class="form-group mb-3">
                <label>Department</label>
                <input type="text" id="field-department" class="form-input" placeholder="e.g. Computer Science" required>
            </div>
            <div class="form-group mb-3">
                <label>Semester</label>
                <input type="text" id="field-semester" class="form-input" placeholder="e.g. Fall 2026" required>
            </div>
        `;
        window.Navigation?.openModal('crud-modal');
    },

    async handleCrudSubmit(e) {
        e.preventDefault();
        try {
            if (modalFormTarget === 'form') {
                const payload = {
                    course: { id: parseInt(document.getElementById('field-course-id').value) },
                    semester: document.getElementById('field-semester').value,
                    status: 'ACTIVE',
                    closingDate: document.getElementById('field-closing-date').value
                };
                await API.post('/api/feedback-forms/create', payload);
                API.showToast('Feedback Form created!', 'success');
                this.loadForms();
            } else if (modalFormTarget === 'question') {
                const payload = {
                    feedbackForm: { id: parseInt(document.getElementById('field-form-id').value) },
                    questionText: document.getElementById('field-question-text').value,
                    maxRating: parseInt(document.getElementById('field-max-rating').value)
                };
                await API.post('/api/questions/create', payload);
                API.showToast('Question created!', 'success');
                this.loadQuestions();
            } else if (modalFormTarget === 'student') {
                const payload = {
                    studentIdentifier: document.getElementById('field-student-id').value,
                    name: document.getElementById('field-student-name').value
                };
                await API.post('/api/students/create', payload);
                API.showToast('Student registered!', 'success');
                this.loadStudents();
            } else if (modalFormTarget === 'course') {
                const payload = {
                    courseCode: document.getElementById('field-course-code').value,
                    courseName: document.getElementById('field-course-name').value,
                    instructor: document.getElementById('field-instructor').value,
                    department: document.getElementById('field-department').value,
                    semester: document.getElementById('field-semester').value
                };
                await API.post('/api/courses/create', payload);
                API.showToast('Course added!', 'success');
                this.loadCourses();
            }

            window.Navigation?.closeModal('crud-modal');
        } catch (error) {
            console.error('Submit error:', error);
        }
    }
};

let modalFormTarget = '';

document.addEventListener('DOMContentLoaded', () => AdminPortal.init());
window.AdminPortal = AdminPortal;
