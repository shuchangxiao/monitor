package edu.hubu.config;

import edu.hubu.task.MonitorJobBean;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfiguration {
    @Bean
    public JobDetail jobDetailFactoryBean(){
        return JobBuilder.newJob(MonitorJobBean.class)
                .withIdentity("monitor-task")
                .storeDurably()
                .build();
    }
    @Bean
    public Trigger cronTriggerFactoryBean(JobDetail jobDetail){
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("*/10 * * * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("monitor-trigger")
                .withSchedule(cronScheduleBuilder)
                .build();
    }
}
