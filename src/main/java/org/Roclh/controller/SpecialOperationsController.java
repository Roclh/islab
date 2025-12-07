package org.Roclh.controller;

import lombok.RequiredArgsConstructor;
import org.Roclh.model.Chapter;
import org.Roclh.model.SpaceMarine;
import org.Roclh.service.ChapterService;
import org.Roclh.service.SpaceMarineService;
import org.Roclh.service.WebSocketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("/special")
@RequiredArgsConstructor
public class SpecialOperationsController {

    private final SpaceMarineService spaceMarineService;
    private final ChapterService chapterService;
    private final WebSocketService webSocketService;

    @GetMapping
    public String specialOperations(Model model) {
        model.addAttribute("chapters", chapterService.findAll());
        return "special/operations";
    }

    @GetMapping("/max-creation")
    public String getMaxCreationDate(Model model) {
        SpaceMarine marine = spaceMarineService.findLatest();
        model.addAttribute("marine", marine);
        return "special/max-creation-result";
    }

    @GetMapping("/chapter-stats")
    public String getChapterStats(Model model) {
        try {
            model.addAttribute("stats", chapterService.getChapters());
        } catch (Exception e) {
            model.addAttribute("stats", new ArrayList<Chapter>());
        }
        return "special/chapter-stats";
    }

    @PostMapping("/health-count")
    public String countByHealth(@RequestParam Float health, Model model) {
        try {
            Long count = spaceMarineService.countByHealthGreaterThan(health);
            model.addAttribute("count", count);
            model.addAttribute("health", health);
        } catch (Exception e) {
            model.addAttribute("count", 0L);
            model.addAttribute("health", health);
        }
        return "special/health-count-result";
    }

    @PostMapping("/create-chapter")
    public String createChapter(@RequestParam String name,
                                @RequestParam(required = false) String parentLegion,
                                @RequestParam String world,
                                @RequestParam Long marinesCount,
                                RedirectAttributes redirectAttributes) {
        try {
            boolean exists = chapterService.findAll().stream()
                    .anyMatch(chapter -> chapter.getName().equalsIgnoreCase(name));

            if (exists) {
                redirectAttributes.addFlashAttribute("error", "Chapter with name '" + name + "' already exists");
            } else {
                Chapter chapter = new Chapter();
                chapter.setName(name);
                chapter.setParentLegion(parentLegion);
                chapter.setWorld(world);
                chapter.setMarinesCount(marinesCount);
                chapterService.save(chapter);
                webSocketService.notifySpaceMarineUpdate();
                redirectAttributes.addFlashAttribute("success", "Chapter '" + name + "' created successfully");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating chapter: " + e.getMessage());
        }
        return "redirect:/special";
    }

    @PostMapping("/delete-chapter")
    public String deleteChapter(@RequestParam Long chapterId,
                                RedirectAttributes redirectAttributes) {
        try {
            Chapter chapter = chapterService.findById(chapterId)
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));

            chapterService.deleteById(chapterId);
            webSocketService.notifySpaceMarineUpdate();
            redirectAttributes.addFlashAttribute("success", "Chapter '" + chapter.getName() + "' deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting chapter: " + e.getMessage());
        }
        return "redirect:/special";
    }
}