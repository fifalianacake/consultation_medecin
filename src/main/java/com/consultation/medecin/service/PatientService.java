package com.consultation.medecin.service;

import com.consultation.medecin.model.Patient;
import com.consultation.medecin.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public List<Patient> getAll() {
        return repository.findAll();
    }

    public Patient getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient introuvable: " + id));
    }

    public Patient create(Patient patient) {
        patient.setId(null);
        return repository.save(patient);
    }

    public Patient update(Integer id, Patient patient) {
        Patient existing = getById(id);
        existing.setNom(patient.getNom());
        existing.setPrenom(patient.getPrenom());
        existing.setDateNaissance(patient.getDateNaissance());
        existing.setSexe(patient.getSexe());
        existing.setTelephone(patient.getTelephone());
        existing.setAdresse(patient.getAdresse());
        return repository.save(existing);
    }

    public void delete(Integer id) {
        repository.delete(getById(id));
    }
}
