package K23CNT1.NguyenXuanVinh.Lesson08.controller;

import K23CNT1.NguyenXuanVinh.Lesson08.entity.Configuration;
import K23CNT1.NguyenXuanVinh.Lesson08.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/configurations")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    @GetMapping
    public String listConfigs(Model model) {
        model.addAttribute("configs", configurationService.getAllConfigurations());
        return "configurations/config-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("config", new Configuration());
        return "configurations/config-form";
    }

    @PostMapping("/new")
    public String saveConfig(@ModelAttribute("config") Configuration config) {
        configurationService.saveConfiguration(config);
        return "redirect:/configurations";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("config", configurationService.getConfigurationById(id));
        return "configurations/config-form";
    }

    @PostMapping("/update/{id}")
    public String updateConfig(@PathVariable Long id, @ModelAttribute("config") Configuration config) {
        config.setId(id);
        configurationService.saveConfiguration(config);
        return "redirect:/configurations";
    }

    @GetMapping("/delete/{id}")
    public String deleteConfig(@PathVariable("id") Long id) {
        configurationService.deleteConfiguration(id);
        return "redirect:/configurations";
    }
}