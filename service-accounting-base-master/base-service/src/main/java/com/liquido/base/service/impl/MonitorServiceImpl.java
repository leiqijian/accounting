package com.liquido.base.service.impl;

import com.liquido.base.pojo.vo.DynamicConstantCreateMonitorVo;
import com.liquido.base.service.MonitorService;
import com.liquido.base.service.monitor.LarkRobotMonitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    private final LarkRobotMonitor larkRobotMonitor;

    @Override
    public void dynamicConstantCreateMonitor(final DynamicConstantCreateMonitorVo vo) {
        String title = "Dynamic Constant Create";
        String info = String.format("**Constant:** %s\\n**Code:** %s",
                vo.getClassSimpleName(), vo.getCode());
        larkRobotMonitor.info(title, info, "");
    }
}
