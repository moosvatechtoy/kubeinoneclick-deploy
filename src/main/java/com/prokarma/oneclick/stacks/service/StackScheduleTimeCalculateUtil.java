package com.prokarma.oneclick.stacks.service;

import com.prokarma.oneclick.stacks.bo.Stack;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class StackScheduleTimeCalculateUtil {

    private static final Logger LOG = LoggerFactory.getLogger(StackScheduleTimeCalculateUtil.class);

    public static void calculateScheduleTime(Stack stack) throws ParseException {
        if (stack.isDeploySchedule()) {
            Date date = getNextValidTime(stack.getDeployScheduleExpression());
            stack.setNextDeployScheduleTime(LocalDateTime.ofInstant(date.toInstant(),
                    ZoneId.of("UTC", ZoneId.SHORT_IDS)));
        }

        if(stack.isDestroySchedule()) {
            Date date = getNextValidTime(stack.getDestroyScheduleExpression());
            stack.setNextDestroyScheduleTime(LocalDateTime.ofInstant(date.toInstant(),
                    ZoneId.of("UTC", ZoneId.SHORT_IDS)));
        }
        if (!stack.isDeploySchedule() && !stack.isDestroySchedule()) {
            if(stack.getDestroyType().equalsIgnoreCase("T")) {
                if(!stack.getDestroyAfterHours().equalsIgnoreCase("-1")) {
                    Date timeNow = new Date();
                    LocalDateTime  destroyTime = LocalDateTime.ofInstant(timeNow.toInstant(),
                            ZoneId.of("UTC", ZoneId.SHORT_IDS));
                    destroyTime = destroyTime.plusHours(Integer.parseInt(stack.getDestroyAfterHours()));
                    stack.setNextDestroyScheduleTime(destroyTime);
                }
            } else {
                String date = StringUtils.join(new Object[]{ stack.getDestroyAfterDate(), stack.getDestroyAfterTime() }, " ");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date formattedDate = formatter.parse(date);
                LocalDateTime  destroyTime = LocalDateTime.ofInstant(formattedDate.toInstant(),
                        ZoneId.of("UTC", ZoneId.SHORT_IDS));
                stack.setNextDestroyScheduleTime(destroyTime);
            }
        }
        stack.setChangeInTTL(false);
    }

    private static Date getNextValidTime(String cron) {
        Date nextValidTime = null;
        try {
            if (StringUtils.isNotBlank(cron)) {
                nextValidTime = new CronExpression(cron).getNextValidTimeAfter(new Date());
            }
        } catch (ParseException e) {
            LOG.warn("Unable to parse the given cron expression: " + cron, e);
        }
        return nextValidTime;
    }
}
