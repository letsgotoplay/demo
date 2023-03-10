package com.mycompany.myapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.JobHistory;
import com.mycompany.myapp.repository.JobHistoryRepository;
import com.mycompany.myapp.repository.search.JobHistorySearchRepository;
import com.mycompany.myapp.service.JobHistoryService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link JobHistory}.
 */
@Service
@Transactional
public class JobHistoryServiceImpl implements JobHistoryService {

    private final Logger log = LoggerFactory.getLogger(JobHistoryServiceImpl.class);

    private final JobHistoryRepository jobHistoryRepository;

    private final JobHistorySearchRepository jobHistorySearchRepository;

    public JobHistoryServiceImpl(JobHistoryRepository jobHistoryRepository, JobHistorySearchRepository jobHistorySearchRepository) {
        this.jobHistoryRepository = jobHistoryRepository;
        this.jobHistorySearchRepository = jobHistorySearchRepository;
    }

    @Override
    public JobHistory save(JobHistory jobHistory) {
        log.debug("Request to save JobHistory : {}", jobHistory);
        JobHistory result = jobHistoryRepository.save(jobHistory);
        jobHistorySearchRepository.index(result);
        return result;
    }

    @Override
    public JobHistory update(JobHistory jobHistory) {
        log.debug("Request to update JobHistory : {}", jobHistory);
        JobHistory result = jobHistoryRepository.save(jobHistory);
        jobHistorySearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<JobHistory> partialUpdate(JobHistory jobHistory) {
        log.debug("Request to partially update JobHistory : {}", jobHistory);

        return jobHistoryRepository
            .findById(jobHistory.getId())
            .map(existingJobHistory -> {
                if (jobHistory.getStartDate() != null) {
                    existingJobHistory.setStartDate(jobHistory.getStartDate());
                }
                if (jobHistory.getEndDate() != null) {
                    existingJobHistory.setEndDate(jobHistory.getEndDate());
                }
                if (jobHistory.getLanguage() != null) {
                    existingJobHistory.setLanguage(jobHistory.getLanguage());
                }

                return existingJobHistory;
            })
            .map(jobHistoryRepository::save)
            .map(savedJobHistory -> {
                jobHistorySearchRepository.save(savedJobHistory);

                return savedJobHistory;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobHistory> findAll() {
        log.debug("Request to get all JobHistories");
        return jobHistoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JobHistory> findOne(Long id) {
        log.debug("Request to get JobHistory : {}", id);
        return jobHistoryRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete JobHistory : {}", id);
        jobHistoryRepository.deleteById(id);
        jobHistorySearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobHistory> search(String query) {
        log.debug("Request to search JobHistories for query {}", query);
        return StreamSupport.stream(jobHistorySearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
