package com.controller;

import com.dao.ExcelReadWrite;
import com.models.EmployeeDetails;
import com.util.Constants;
import com.models.Employee;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Controller
public class ReadWriteController {
    Logger logger = LoggerFactory.getLogger(ReadWriteController.class);
    @Autowired
    ExcelReadWrite excelReadWrite;
    int count = 0;

    @GetMapping("logTime")
    public String logTime(@RequestParam @NotNull(message = "LID is mandatory") @Pattern(regexp =
            "^L[0-9]*$") @Size(min= 7, max = 7, message = "LID Should be valid length") String lid, @RequestParam
            String time, @RequestParam String eventType) {
        DateFormat dayFormatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat timeFormatter = new SimpleDateFormat("hh:mm");
        long milliSeconds = Long.parseLong(time);
        System.out.println(milliSeconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        System.out.println(String.valueOf(dayFormatter.format(calendar.getTime())));
        System.out.println(String.valueOf(timeFormatter.format(calendar.getTime())));
        Employee emp = new Employee();
        emp.setLid(lid);
        emp.setDay(String.valueOf(dayFormatter.format(calendar.getTime())));
        if (eventType.equalsIgnoreCase("login")) {
            emp.setLoginTime(String.valueOf(timeFormatter.format(calendar.getTime())));
        } else if (eventType.equalsIgnoreCase("logoff")) {
            emp.setLogoffTime(String.valueOf(timeFormatter.format(calendar.getTime())));
        }
        logger.info("Employee Object: {}", emp);
        try {
            if (fileExists()) {
                excelReadWrite.update(emp, eventType);
            } else {
                excelReadWrite.write(emp);
            }
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    @GetMapping("getAllLids")
    @ResponseBody
    public List<EmployeeDetails> getAllLids(){
        try {
            return excelReadWrite.readLidDetails();
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
            logger.info("Unable to fetch LID data");
            return null;
        }
    }

    public boolean fileExists() {
        File file = new File(Constants.FILE_NAME);
        logger.info("Files exists? {}", file.exists());
        return file.exists();
    }

}
