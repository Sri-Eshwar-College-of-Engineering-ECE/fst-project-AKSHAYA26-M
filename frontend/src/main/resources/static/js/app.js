const API_BASE = 'http://localhost:8080/api';

let selectedSymptoms = new Set();
let selectedSeverity = 'mild';

// ===== PAGE NAVIGATION =====
function showPage(page) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
    document.getElementById('page-' + page).classList.add('active');
    event.target.classList.add('active');

    if (page === 'history') loadHistory();
    if (page === 'stats') loadStats();
}

// ===== LOAD SYMPTOMS FROM API =====
async function loadSymptoms() {
    try {
        const res = await fetch(`${API_BASE}/symptoms`);
        const symptoms = await res.json();
        renderSymptomGrid(symptoms);
    } catch (e) {
        // Fallback symptoms if backend not running
        const fallback = [
            'fever', 'headache', 'cough', 'fatigue', 'nausea', 'vomiting',
            'body ache', 'sore throat', 'runny nose', 'sneezing', 'dizziness',
            'chest pain', 'shortness of breath', 'stomach pain', 'diarrhea',
            'rash', 'itching', 'weakness', 'chills', 'sweating',
            'loss of appetite', 'bloating', 'dry mouth', 'blurred vision',
            'back pain', 'joint pain', 'high fever', 'eye pain', 'dark urine'
        ];
        renderSymptomGrid(fallback);
    }
}

function renderSymptomGrid(symptoms) {
    const grid = document.getElementById('symptomGrid');
    grid.innerHTML = '';
    symptoms.forEach(symptom => {
        const tag = document.createElement('div');
        tag.className = 'symptom-tag';
        tag.textContent = symptom;
        tag.onclick = () => toggleSymptom(symptom, tag);
        grid.appendChild(tag);
    });
}

function toggleSymptom(symptom, el) {
    if (selectedSymptoms.has(symptom)) {
        selectedSymptoms.delete(symptom);
        el.classList.remove('selected');
    } else {
        selectedSymptoms.add(symptom);
        el.classList.add('selected');
    }
}

function addCustomSymptom() {
    const input = document.getElementById('customSymptom');
    const val = input.value.trim().toLowerCase();
    if (!val) return;

    const grid = document.getElementById('symptomGrid');
    const tag = document.createElement('div');
    tag.className = 'symptom-tag selected';
    tag.textContent = val;
    tag.onclick = () => toggleSymptom(val, tag);
    grid.appendChild(tag);
    selectedSymptoms.add(val);
    input.value = '';
}

// ===== SEVERITY =====
function selectSeverity(severity, el) {
    document.querySelectorAll('.severity-card').forEach(c => c.classList.remove('selected'));
    el.classList.add('selected');
    selectedSeverity = severity;
}

// ===== ANALYZE =====
async function analyzeSymptoms() {
    const name = document.getElementById('patientName').value.trim();
    const age = document.getElementById('patientAge').value;
    const gender = document.getElementById('gender').value;
    const duration = document.getElementById('duration').value;

    if (!name) { alert('Please enter your name'); return; }
    if (!age) { alert('Please enter your age'); return; }
    if (!gender) { alert('Please select your gender'); return; }
    if (selectedSymptoms.size === 0) { alert('Please select at least one symptom'); return; }

    const btn = document.querySelector('.btn-analyze');
    btn.textContent = '🔬 Analyzing...';
    btn.disabled = true;

    const payload = {
        patientName: name,
        patientAge: parseInt(age),
        gender: gender,
        symptoms: Array.from(selectedSymptoms),
        severity: selectedSeverity,
        duration: duration
    };

    try {
        const res = await fetch(`${API_BASE}/check`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!res.ok) throw new Error('API error');
        const result = await res.json();
        showResult(result);
    } catch (e) {
        // Demo mode - frontend-only fallback
        const mockResult = analyzeMock(payload);
        showResult(mockResult);
    } finally {
        btn.innerHTML = '<span>🔬 Analyze My Symptoms</span>';
        btn.disabled = false;
    }
}

// Fallback mock analysis (works without backend)
function analyzeMock(payload) {
    const conditionMap = {
        'fever': 'Viral Fever',
        'headache': 'Migraine',
        'cough': 'Common Cold',
        'nausea': 'Gastritis',
        'vomiting': 'Food Poisoning',
        'body ache': 'Influenza (Flu)',
        'dizziness': 'Dehydration',
        'chest pain': 'Anxiety/Stress',
        'rash': 'Allergic Reaction',
        'high fever': 'Dengue Fever'
    };

    const recMap = {
        'Viral Fever': 'Take paracetamol, rest, and drink plenty of fluids. Visit a doctor if fever exceeds 103°F.',
        'Migraine': 'Rest in a dark room, apply cold compress. Consult a neurologist if frequent.',
        'Common Cold': 'Rest, drink warm fluids and take OTC cold medicine. See a doctor if symptoms persist.',
        'Gastritis': 'Eat smaller meals, avoid spicy/fatty food. Consult a doctor if pain persists.',
        'Food Poisoning': 'Stay hydrated with ORS/electrolytes. See a doctor if vomiting is severe.',
        'Influenza (Flu)': 'Rest, stay hydrated, take paracetamol. Consult a doctor if breathing is difficult.',
        'Dehydration': 'Drink plenty of water and electrolyte solutions. Seek care if severe.',
        'Anxiety/Stress': 'Practice deep breathing and meditation. Consider speaking to a professional.',
        'Allergic Reaction': 'Avoid the allergen, take antihistamines. Seek emergency care if throat swells.',
        'Dengue Fever': 'Immediate medical attention required! Monitor platelet count.',
        'General Illness': 'Please consult a qualified healthcare professional for proper diagnosis.'
    };

    let condition = 'General Illness';
    for (const sym of payload.symptoms) {
        const matched = conditionMap[sym.toLowerCase()];
        if (matched) { condition = matched; break; }
    }

    let recommendation = recMap[condition] || recMap['General Illness'];
    if (payload.severity === 'severe') {
        recommendation = '⚠️ URGENT: Your symptoms are severe. Please visit the nearest hospital immediately. ' + recommendation;
    }

    return {
        patientName: payload.patientName,
        patientAge: payload.patientAge,
        gender: payload.gender,
        symptoms: payload.symptoms,
        severity: payload.severity,
        duration: payload.duration,
        possibleCondition: condition,
        recommendation: recommendation
    };
}

function showResult(result) {
    document.getElementById('resultCondition').textContent = result.possibleCondition;
    document.getElementById('resultName').textContent = result.patientName + ' (' + result.gender + ')';
    document.getElementById('resultAge').textContent = result.patientAge + ' years old';
    document.getElementById('resultDuration').textContent = result.duration;
    document.getElementById('resultSeverity').textContent = result.severity.charAt(0).toUpperCase() + result.severity.slice(1);
    document.getElementById('resultRecommendation').textContent = result.recommendation;

    const tagList = document.getElementById('resultSymptoms');
    tagList.innerHTML = result.symptoms.map(s => `<span>${s}</span>`).join('');

    const container = document.getElementById('resultContainer');
    container.style.display = 'block';
    container.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function resetForm() {
    document.getElementById('patientName').value = '';
    document.getElementById('patientAge').value = '';
    document.getElementById('gender').value = '';
    selectedSymptoms.clear();
    document.querySelectorAll('.symptom-tag').forEach(t => t.classList.remove('selected'));
    document.querySelectorAll('.severity-card').forEach(c => c.classList.remove('selected'));
    selectedSeverity = 'mild';
    document.getElementById('resultContainer').style.display = 'none';
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// ===== HISTORY =====
async function loadHistory() {
    const container = document.getElementById('historyContainer');
    container.innerHTML = '<div class="loading">Loading history...</div>';

    try {
        const res = await fetch(`${API_BASE}/history`);
        const history = await res.json();

        if (history.length === 0) {
            container.innerHTML = `<div class="empty-state"><div class="empty-icon">📋</div><p>No checks yet. Start your first symptom check!</p></div>`;
            return;
        }

        container.innerHTML = history.map(h => `
            <div class="history-item">
                <div>
                    <div class="history-name">👤 ${h.patientName}</div>
                    <div class="history-meta">${h.patientAge} yrs • ${h.gender} • ${h.duration}</div>
                    <div class="history-meta" style="margin-top:0.3rem">Symptoms: ${h.symptoms ? h.symptoms.join(', ') : ''}</div>
                </div>
                <div style="text-align:right">
                    <div class="history-condition">${h.possibleCondition}</div>
                    <div class="history-meta" style="margin-top:0.4rem">Severity: ${h.severity}</div>
                    <div class="history-meta">${h.checkedAt ? new Date(h.checkedAt).toLocaleString() : 'Just now'}</div>
                </div>
            </div>
        `).join('');
    } catch (e) {
        container.innerHTML = `<div class="empty-state"><div class="empty-icon">⚠️</div><p>Backend not connected. Start the Spring Boot server to see history.</p></div>`;
    }
}

// ===== STATS =====
async function loadStats() {
    try {
        const res = await fetch(`${API_BASE}/stats`);
        const stats = await res.json();

        document.getElementById('totalChecks').textContent = stats.totalChecks || 0;
        document.getElementById('recentChecks').textContent = stats.recentChecks || 0;

        const chart = document.getElementById('conditionChart');
        const dist = stats.conditionDistribution || {};
        const sorted = Object.entries(dist).sort((a, b) => b[1] - a[1]);
        const max = sorted[0]?.[1] || 1;

        if (sorted.length === 0) {
            chart.innerHTML = '<div class="empty-state"><p>No data yet. Do a few symptom checks first!</p></div>';
            return;
        }

        chart.innerHTML = sorted.slice(0, 8).map(([condition, count]) => `
            <div class="chart-row">
                <div class="chart-label">${condition}</div>
                <div class="chart-bar-wrap">
                    <div class="chart-bar" style="width: ${(count/max)*100}%">${count}</div>
                </div>
                <div class="chart-count">${count}</div>
            </div>
        `).join('');
    } catch (e) {
        document.getElementById('totalChecks').textContent = '-';
        document.getElementById('recentChecks').textContent = '-';
        document.getElementById('conditionChart').innerHTML = '<div class="empty-state"><p>Backend not connected. Start the Spring Boot server to see stats.</p></div>';
    }
}

// ===== INIT =====
document.addEventListener('DOMContentLoaded', () => {
    loadSymptoms();
    // Auto-select first severity
    const mildCard = document.querySelector('.severity-card.mild');
    if (mildCard) mildCard.classList.add('selected');
});
