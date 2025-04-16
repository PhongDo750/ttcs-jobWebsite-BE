package com.example.ttcs_jobwebsite.mapper;

import com.example.ttcs_jobwebsite.dto.job.JobInput;
import com.example.ttcs_jobwebsite.dto.job.JobOutputV1;
import com.example.ttcs_jobwebsite.dto.job.JobOutputV2;
import com.example.ttcs_jobwebsite.entity.JobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface JobMapper {
    JobEntity getEntityFromInput(JobInput jobInput);
    void updateEntityFromInput(@MappingTarget JobEntity jobEntity, JobInput jobInput);
    JobOutputV2 getOutputV2FromEntity(JobEntity jobEntity);
    JobOutputV1 getOutputV1FromEntity(JobEntity jobEntity);
}
