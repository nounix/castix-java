package rpi_cast.controllers;

import rpi_cast.common.Tools;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;

@Controller
public class RpiCastController {

	@GetMapping("/")
    public String redirectToMain() {
        return "redirect:rpi-cast";
    }

    @GetMapping("/rpi-cast")
    public String mainForm() {
        return "rpi-cast";
    }

    @GetMapping("/rpi-cast/close-win")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void closeWin() {
        Tools.runCommand(false, "xdotool getactivewindow windowkill");
    }

    @PostMapping(value = "/rpi-cast/ctrl-mouse")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void ctrlMouse(@RequestBody List<Integer> points) {
        int x1 = points.get(0), x2 = points.get(1), y1 = points.get(2), y2 = points.get(3);
    	int x3 = x2 - x1;
    	int y3 = y2 - y1;

    	if(x1 == x2 && y1 == y2) Tools.runCommand(false, "xdotool click 1");
    	else Tools.runCommand(false, "xdotool mousemove_relative -- " + x3 + " " + y3);
    }

    @PostMapping(value = "/rpi-cast/play-video")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void playVideo(@RequestParam("data") String url) {
    	Tools.runCommand(true, "mpv " + url);
    }

    @PostMapping(value = "/rpi-cast/type-string")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void typeString(@RequestParam("data") String str) {
        str = str.replaceAll("\'","\'\\\\\'\'");
    	Tools.runCommand(false, "xdotool type '" + str + "'");
    }

    @PostMapping(value = "/rpi-cast/press-key")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void pressKey(@RequestParam("data") String key) {
        Tools.runCommand(false, "xdotool key '" + key + "'");
    }

    @PostMapping(value = "/rpi-cast/press-mouseKey")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void pressMouseKey(@RequestParam("data") String key) {
        Tools.runCommand(false, "xdotool click '" + key + "'");
    }
}
