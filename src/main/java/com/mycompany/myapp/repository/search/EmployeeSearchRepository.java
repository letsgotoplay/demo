package com.mycompany.myapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.repository.EmployeeRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Employee} entity.
 */
public interface EmployeeSearchRepository extends ElasticsearchRepository<Employee, Long>, EmployeeSearchRepositoryInternal {}

interface EmployeeSearchRepositoryInternal {
    Stream<Employee> search(String query);

    Stream<Employee> search(Query query);

    void index(Employee entity);
}

class EmployeeSearchRepositoryInternalImpl implements EmployeeSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final EmployeeRepository repository;

    EmployeeSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, EmployeeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Employee> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Employee> search(Query query) {
        return elasticsearchTemplate.search(query, Employee.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Employee entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
