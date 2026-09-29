/**
 * Feedback Submission Engine
 * Manages loading questions for a form, rating selections, and 2-step API response submission:
 * Step 1: POST /api/responses/create
 * Step 2: POST /api/answers/create for each question rating
 */

const FeedbackManager = {
    currentFormId: null,

    init() {
        const form = document.getElementById('feedback-submit-form');
        if (form) {
            form.addEventListener('submit', (e) => this.handleSubmit(e));
        }
    },

    async openSubmissionModal(formId) {
        this.currentFormId = formId;
        document.getElementById('feedback-form-id').value = formId;

        const modalTitle = document.getElementById('feedback-modal-title');
        const questionsContainer = document.getElementById('feedback-questions-container');

        if (modalTitle) modalTitle.textContent = `Course Feedback Form #${formId}`;
        if (questionsContainer) {
            questionsContainer.innerHTML = `<div class="p-4 text-center">Loading questions...</div>`;
        }

        window.Navigation?.openModal('feedback-modal');

        try {
            // Fetch questions for this form
            const allQuestions = await API.get('/api/questions/getAll');
            const qList = Array.isArray(allQuestions) ? allQuestions.filter(q => q.feedbackForm && q.feedbackForm.id == formId) : [];

            if (qList.length === 0) {
                questionsContainer.innerHTML = `
                    <div class="p-4 text-center text-secondary">
                        No evaluation questions registered for this form yet.
                    </div>
                `;
                return;
            }

            questionsContainer.innerHTML = qList.map((q, idx) => `
                <div class="question-card glass-card p-4 mb-3" data-q-id="${q.id}">
                    <div class="question-header mb-2 flex-between">
                        <strong>Q${idx + 1}. ${q.questionText}</strong>
                        <span class="badge badge-accent">Max: ${q.maxRating || 5} Stars</span>
                    </div>
                    <div class="rating-options flex-gap" data-question-id="${q.id}">
                        ${[1, 2, 3, 4, 5].map(star => `
                            <label class="rating-pill">
                                <input type="radio" name="q_${q.id}" value="${star}" ${star === 5 ? 'checked' : ''}>
                                <span class="pill-label">★ ${star}</span>
                            </label>
                        `).join('')}
                    </div>
                </div>
            `).join('');

        } catch (error) {
            console.error('Failed to load questions:', error);
            if (questionsContainer) {
                questionsContainer.innerHTML = `<div class="p-3 text-error">Error loading feedback questions.</div>`;
            }
        }
    },

    async handleSubmit(e) {
        e.preventDefault();
        const user = AuthManager.getCurrentUser();
        const studentId = user && user.role === 'student' ? (user.id || 1) : 1;
        const formId = this.currentFormId;

        if (!formId) return;

        try {
            // Step 1: Create Response record
            const responsePayload = {
                student: { id: parseInt(studentId) },
                feedbackForm: { id: parseInt(formId) }
            };

            const createdResponse = await API.post('/api/responses/create', responsePayload);
            const responseId = createdResponse.id;

            // Step 2: Extract ratings and post Answers
            const qCards = document.querySelectorAll('.question-card[data-q-id]');
            const answerPromises = [];

            qCards.forEach(card => {
                const qId = card.dataset.qId;
                const checkedRadio = card.querySelector(`input[name="q_${qId}"]:checked`);
                const rating = checkedRadio ? parseInt(checkedRadio.value) : 5;

                const answerPayload = {
                    response: { id: responseId },
                    question: { id: parseInt(qId) },
                    rating: rating
                };

                answerPromises.push(API.post('/api/answers/create', answerPayload));
            });

            await Promise.all(answerPromises);

            API.showToast('Feedback submitted successfully! Thank you.', 'success');
            window.Navigation?.closeModal('feedback-modal');

            // Refresh student views
            if (window.StudentPortal) {
                window.StudentPortal.loadData();
            }

        } catch (error) {
            console.error('Feedback submission failed:', error);
            API.showToast('Failed to submit feedback. Please try again.', 'error');
        }
    }
};

document.addEventListener('DOMContentLoaded', () => FeedbackManager.init());
window.FeedbackManager = FeedbackManager;
