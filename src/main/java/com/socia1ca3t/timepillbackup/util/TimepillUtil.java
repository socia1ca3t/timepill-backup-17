package com.socia1ca3t.timepillbackup.util;

import com.socia1ca3t.timepillbackup.pojo.dto.Diary;
import com.socia1ca3t.timepillbackup.pojo.dto.NoteBook;
import com.socia1ca3t.timepillbackup.pojo.vo.NotebooksOfOneYear;
import com.socia1ca3t.timepillbackup.properties.TimepillConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class TimepillUtil {


    private static TimepillConfig config;

    @Autowired
    TimepillUtil(TimepillConfig config) {
        TimepillUtil.config = config;
    }

    public static TimepillConfig getConfig() {

        if (TimepillUtil.config == null) {
            throw new RuntimeException("TimepillConfig未初始化");
        }

        return TimepillUtil.config;
    }


    public static NoteBook getNotebookById(List<NoteBook> list, int id) {

        return list.stream().filter(p -> id == p.getId())
                .findFirst()
                .orElse(null);
    }

    public static List<NotebooksOfOneYear> groupByYear(List<NoteBook> noteBooks) {

        final List<NotebooksOfOneYear> notebooksGroup = new ArrayList<>();
        for (NoteBook noteBook : noteBooks) {

            int year = LocalDate.parse(noteBook.getCreateDate()).getYear();

            if (notebooksGroup.isEmpty() || notebooksGroup.stream().noneMatch(group -> group.getYear() == year)) {

                List<NoteBook> list = new ArrayList<>();
                list.add(noteBook);
                notebooksGroup.add(new NotebooksOfOneYear(year, list));
            } else {

                List<NotebooksOfOneYear> notebooksGroups = notebooksGroup.stream().filter(group -> group.getYear() == year).toList();
                notebooksGroups.get(0).getNoteBooks().add(noteBook);
            }
        }
        return notebooksGroup.stream()
                .sorted((g1, g2) -> Integer.compare(g2.getYear(), g1.getYear()))
                .collect(Collectors.toList());
    }


    public static String render2html(Map<String, Object> map, String templateName) {


        return TemplateUtil.render2html(map, templateName);
    }


    public static String render2html(final NoteBook noteBook, final List<Diary> diarys) {

        final Map<String, Object> context = new HashMap<>();
        context.put("diarys", diarys);
        context.put("notebook", noteBook);
        context.put("download", true);

        return TemplateUtil.render2html(context, "diary_index");
    }


    public static String render2html(final List<Diary> diarys, String templateName) {

        Context context = new Context();
        context.setVariable("diarys", diarys);
        context.setVariable("notebookName", diarys.get(0).getNotebookName());

        return TemplateUtil.render2html(context, templateName);
    }

}
