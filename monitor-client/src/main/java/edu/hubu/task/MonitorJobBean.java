package edu.hubu.task;

import edu.hubu.entity.RuntimeDetail;
import edu.hubu.utils.MonitorUtils;
import edu.hubu.utils.NetUtils;
import jakarta.annotation.Resource;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class MonitorJobBean extends QuartzJobBean {
    @Resource
    MonitorUtils monitorUtils;
    @Resource
    NetUtils netUtils;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        RuntimeDetail runtimeDetail = monitorUtils.monitorRuntimeDetail();
        System.out.println(runtimeDetail);
        netUtils.updateRuntimeDetails(runtimeDetail);
    }
}
