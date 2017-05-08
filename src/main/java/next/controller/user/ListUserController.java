package next.controller.user;

import javax.servlet.http.HttpSession;

import next.controller.UserSessionUtils;
import next.dao.UserDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ListUserController {
	private UserDao userDao = UserDao.getInstance();

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String index(HttpSession session, Model model) throws Exception {
    	if (!UserSessionUtils.isLogined(session)) {
			return "redirect:/users/loginForm";
		}

        ModelAndView mav = new org.springframework.web.servlet.ModelAndView("index");
        mav.addObject("users", userDao.findAll());
        return mav.getViewName();
    }
}
