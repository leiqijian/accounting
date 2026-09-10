package com.liquido.worker.service.impl;

import com.liquido.worker.pojo.entity.TaskLogFeeCalculation;
import com.liquido.worker.repository.TaskLogFeeCalculationRepository;
import com.liquido.worker.service.TaskLogFeeCalculationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskLogFeeCalculationServiceImpl implements TaskLogFeeCalculationService {

    private final TaskLogFeeCalculationRepository taskLogFeeCalculationRepository;

    @Override
    public void saveLog(final TaskLogFeeCalculation log) {
        taskLogFeeCalculationRepository.save(log);
    }

}
