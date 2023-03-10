package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.JobHistory;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link JobHistory}.
 */
public interface JobHistoryService {
    /**
     * Save a jobHistory.
     *
     * @param jobHistory the entity to save.
     * @return the persisted entity.
     */
    JobHistory save(JobHistory jobHistory);

    /**
     * Updates a jobHistory.
     *
     * @param jobHistory the entity to update.
     * @return the persisted entity.
     */
    JobHistory update(JobHistory jobHistory);

    /**
     * Partially updates a jobHistory.
     *
     * @param jobHistory the entity to update partially.
     * @return the persisted entity.
     */
    Optional<JobHistory> partialUpdate(JobHistory jobHistory);

    /**
     * Get all the jobHistories.
     *
     * @return the list of entities.
     */
    List<JobHistory> findAll();

    /**
     * Get the "id" jobHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<JobHistory> findOne(Long id);

    /**
     * Delete the "id" jobHistory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the jobHistory corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<JobHistory> search(String query);
}
