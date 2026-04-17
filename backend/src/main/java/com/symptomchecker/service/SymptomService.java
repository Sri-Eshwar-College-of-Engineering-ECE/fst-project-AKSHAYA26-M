package com.symptomchecker.service;

import com.symptomchecker.model.SymptomCheck;
import com.symptomchecker.model.SymptomRequest;
import com.symptomchecker.repository.SymptomCheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SymptomService {

    @Autowired
    private SymptomCheckRepository repository;

    // Symptom-to-condition mapping
    private static final Map<String, List<String>> CONDITION_SYMPTOMS = new LinkedHashMap<>();
    private static final Map<String, String> CONDITION_RECOMMENDATIONS = new HashMap<>();

    static {
        CONDITION_SYMPTOMS.put("Common Cold", Arrays.asList("runny nose", "sneezing", "sore throat", "cough", "mild fever"));
        CONDITION_SYMPTOMS.put("Influenza (Flu)", Arrays.asList("high fever", "body ache", "fatigue", "headache", "cough", "chills"));
        CONDITION_SYMPTOMS.put("Migraine", Arrays.asList("headache", "nausea", "sensitivity to light", "dizziness", "vomiting"));
        CONDITION_SYMPTOMS.put("Food Poisoning", Arrays.asList("nausea", "vomiting", "diarrhea", "stomach pain", "fever", "weakness"));
        CONDITION_SYMPTOMS.put("Viral Fever", Arrays.asList("high fever", "body ache", "fatigue", "chills", "headache"));
        CONDITION_SYMPTOMS.put("Dehydration", Arrays.asList("dizziness", "dry mouth", "fatigue", "headache", "weakness", "dark urine"));
        CONDITION_SYMPTOMS.put("Allergic Reaction", Arrays.asList("sneezing", "runny nose", "itching", "rash", "watery eyes"));
        CONDITION_SYMPTOMS.put("Hypertension (High BP)", Arrays.asList("headache", "dizziness", "chest pain", "blurred vision", "shortness of breath"));
        CONDITION_SYMPTOMS.put("Diabetes (Low Sugar)", Arrays.asList("weakness", "sweating", "dizziness", "hunger", "confusion", "trembling"));
        CONDITION_SYMPTOMS.put("Gastritis", Arrays.asList("stomach pain", "nausea", "bloating", "vomiting", "heartburn", "loss of appetite"));
        CONDITION_SYMPTOMS.put("Anxiety/Stress", Arrays.asList("chest pain", "shortness of breath", "headache", "fatigue", "dizziness", "sweating", "trembling"));
        CONDITION_SYMPTOMS.put("UTI (Urinary Tract Infection)", Arrays.asList("burning urination", "frequent urination", "lower back pain", "fever", "pelvic pain"));
        CONDITION_SYMPTOMS.put("Dengue Fever", Arrays.asList("high fever", "severe headache", "body ache", "rash", "eye pain", "fatigue"));
        CONDITION_SYMPTOMS.put("Asthma", Arrays.asList("shortness of breath", "wheezing", "cough", "chest tightness", "difficulty breathing"));
        CONDITION_SYMPTOMS.put("Malaria", Arrays.asList("high fever", "chills", "sweating", "headache", "nausea", "vomiting", "body ache"));

        CONDITION_RECOMMENDATIONS.put("Common Cold", "Rest well, drink warm fluids, and take OTC cold medicine. See a doctor if symptoms persist beyond 7 days.");
        CONDITION_RECOMMENDATIONS.put("Influenza (Flu)", "Rest, stay hydrated, and take paracetamol for fever. Consult a doctor if fever is very high or breathing is difficult.");
        CONDITION_RECOMMENDATIONS.put("Migraine", "Rest in a dark quiet room, apply cold compress, stay hydrated. Consult a neurologist if migraines are frequent.");
        CONDITION_RECOMMENDATIONS.put("Food Poisoning", "Stay hydrated with ORS/electrolytes, avoid solid food temporarily. See a doctor if vomiting/diarrhea is severe.");
        CONDITION_RECOMMENDATIONS.put("Viral Fever", "Take paracetamol, rest, and drink fluids. Visit a doctor if fever exceeds 103°F or lasts more than 3 days.");
        CONDITION_RECOMMENDATIONS.put("Dehydration", "Drink plenty of water and electrolyte solutions. Seek immediate care if symptoms are severe.");
        CONDITION_RECOMMENDATIONS.put("Allergic Reaction", "Avoid the allergen, take antihistamines. Seek emergency care if there's throat swelling or difficulty breathing.");
        CONDITION_RECOMMENDATIONS.put("Hypertension (High BP)", "Monitor blood pressure, reduce salt intake. Consult a cardiologist immediately for chest pain.");
        CONDITION_RECOMMENDATIONS.put("Diabetes (Low Sugar)", "Consume sugar/glucose immediately. See a doctor to adjust your diabetes management plan.");
        CONDITION_RECOMMENDATIONS.put("Gastritis", "Eat smaller meals, avoid spicy/fatty food and alcohol. Consult a gastroenterologist if pain persists.");
        CONDITION_RECOMMENDATIONS.put("Anxiety/Stress", "Practice deep breathing, meditation, and adequate sleep. Consider speaking to a mental health professional.");
        CONDITION_RECOMMENDATIONS.put("UTI (Urinary Tract Infection)", "Drink plenty of water. Consult a doctor for appropriate antibiotics - do not self-medicate.");
        CONDITION_RECOMMENDATIONS.put("Dengue Fever", "Immediate medical attention required! Monitor platelet count. Stay hydrated and avoid aspirin/ibuprofen.");
        CONDITION_RECOMMENDATIONS.put("Asthma", "Use prescribed inhaler, avoid triggers. Go to emergency if breathing is severely compromised.");
        CONDITION_RECOMMENDATIONS.put("Malaria", "Seek immediate medical treatment! Malaria requires prescription anti-malarial medication.");
    }

    public SymptomCheck analyzeSymptoms(SymptomRequest request) {
        List<String> userSymptoms = request.getSymptoms().stream()
                .map(String::toLowerCase)
                .toList();

        // Score each condition by how many symptoms match
        Map<String, Integer> scores = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : CONDITION_SYMPTOMS.entrySet()) {
            int matchCount = 0;
            for (String condSymptom : entry.getValue()) {
                for (String userSymptom : userSymptoms) {
                    if (userSymptom.contains(condSymptom) || condSymptom.contains(userSymptom)) {
                        matchCount++;
                        break;
                    }
                }
            }
            if (matchCount > 0) {
                scores.put(entry.getKey(), matchCount);
            }
        }

        // Find best match
        String bestCondition = "General Illness";
        String recommendation = "Please consult a doctor for proper diagnosis. Maintain rest and hydration.";

        if (!scores.isEmpty()) {
            bestCondition = scores.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .get().getKey();
            recommendation = CONDITION_RECOMMENDATIONS.getOrDefault(bestCondition, recommendation);
        }

        // Adjust recommendation based on severity
        if ("severe".equalsIgnoreCase(request.getSeverity())) {
            recommendation = "⚠️ URGENT: Your symptoms are severe. Please visit the nearest hospital or emergency room immediately. " + recommendation;
        }

        SymptomCheck check = new SymptomCheck();
        check.setPatientName(request.getPatientName());
        check.setPatientAge(request.getPatientAge());
        check.setGender(request.getGender());
        check.setSymptoms(request.getSymptoms());
        check.setSeverity(request.getSeverity());
        check.setDuration(request.getDuration());
        check.setPossibleCondition(bestCondition);
        check.setRecommendation(recommendation);

        return repository.save(check);
    }

    public List<SymptomCheck> getRecentChecks() {
        return repository.findTop10ByOrderByCheckedAtDesc();
    }

    public List<String> getAllSymptoms() {
        Set<String> allSymptoms = new LinkedHashSet<>();
        for (List<String> symptoms : CONDITION_SYMPTOMS.values()) {
            allSymptoms.addAll(symptoms);
        }
        return new ArrayList<>(allSymptoms);
    }

    public Map<String, Object> getStats() {
        long total = repository.count();
        List<SymptomCheck> recent = repository.findTop10ByOrderByCheckedAtDesc();

        Map<String, Integer> conditionCount = new HashMap<>();
        repository.findAll().forEach(check -> {
            conditionCount.merge(check.getPossibleCondition(), 1, Integer::sum);
        });

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalChecks", total);
        stats.put("recentChecks", recent.size());
        stats.put("conditionDistribution", conditionCount);
        return stats;
    }
}
