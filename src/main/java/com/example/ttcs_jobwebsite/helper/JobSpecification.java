package com.example.ttcs_jobwebsite.helper;

import com.example.ttcs_jobwebsite.entity.JobEntity;
import com.example.ttcs_jobwebsite.exceptionhandler.AppException;
import com.example.ttcs_jobwebsite.exceptionhandler.ErrorCode;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class JobSpecification {
    public static Specification<JobEntity> filterJobs(
            String jobName, String occupationName, String experience, String province,
            String jobType, String jobLevel, Double minSalary, Double maxSalary,
            String educationLevel) {

        return (Root<JobEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (jobName != null && !jobName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("jobName")), "%" + jobName.toLowerCase() + "%"));
            }
            if (occupationName != null && !occupationName.isEmpty()) {
                predicates.add(cb.equal(root.get("occupationName"), occupationName));
            }
            if (experience != null && !experience.isEmpty()) {
                predicates.add(cb.equal(root.get("experience"), experience));
            }
            if (province != null && !province.isEmpty()) {
                predicates.add(cb.equal(root.get("province"), province));
            }
            if (jobType != null && !jobType.isEmpty()) {
                predicates.add(cb.equal(root.get("jobType"), jobType));
            }
            if (jobLevel != null && !jobLevel.isEmpty()) {
                predicates.add(cb.equal(root.get("jobLevel"), jobLevel));
            }
            if (educationLevel != null && !educationLevel.isEmpty()) {
                predicates.add(cb.equal(root.get("educationLevel"), educationLevel));
            }

            // Xử lý điều kiện minSalary & maxSalary
            if (minSalary != null && maxSalary != null) {

                if (minSalary > maxSalary) {
                    throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_SALARY);
                }

                predicates.add(cb.or(
                        cb.between(root.get("minSalary"), minSalary, maxSalary),
                        cb.between(root.get("maxSalary"), minSalary, maxSalary)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
