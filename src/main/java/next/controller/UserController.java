package next.controller;

import next.dao.UserDao;
import next.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private UserDao userDao = UserDao.getInstance();

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String index(HttpSession session) throws Exception {
        if (!UserSessionUtils.isLogined(session)) {
            return "redirect:/users/loginForm";
        }

        ModelAndView mav = new ModelAndView("index");
        mav.addObject("users", userDao.findAll());
        return mav.getViewName();
    }

    @RequestMapping(value = "/users/loginForm", method = RequestMethod.GET)
    public ModelAndView loginForm() throws Exception {
        return new ModelAndView("user/login");
    }

    @RequestMapping(value = "/users/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request) throws Exception {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        User user = userDao.findByUserId(userId);

        if (user == null) {
            throw new NullPointerException("사용자를 찾을 수 없습니다.");
        }

        if (user.matchPassword(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            return "redirect:/";
        } else {
            throw new IllegalStateException("비밀번호가 틀립니다.");
        }
    }

    @RequestMapping(value = "/users/form", method = RequestMethod.GET)
    public ModelAndView registerForm() {
        return new ModelAndView("user/form");
    }

    @RequestMapping(value = "/users/create", method = RequestMethod.POST)
    public String register(HttpServletRequest request) {
        User user = new User(
                request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email"));
        log.debug("User : {}", user);
        userDao.insert(user);
        return "redirect:/";
    }

    @RequestMapping(value = "/users/profile", method = RequestMethod.GET)
    public ModelAndView profile(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        ModelAndView mav = new ModelAndView("user/profile");
        mav.addObject("user", userDao.findByUserId(userId));
        return mav;
    }

    @RequestMapping(value = "/users/updateForm", method = RequestMethod.GET)
    public ModelAndView updateForm(HttpServletRequest request) {
        User user = userDao.findByUserId(request.getParameter("userId"));

        if (!UserSessionUtils.isSameUser(request.getSession(), user)) {
            throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
        }
        ModelAndView mav = new ModelAndView("user/updateForm");
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/users/update", method = RequestMethod.POST)
    public String update(HttpServletRequest req) {
        User user = userDao.findByUserId(req.getParameter("userId"));

        if (!UserSessionUtils.isSameUser(req.getSession(), user)) {
            throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
        }

        User updateUser = new User(
                req.getParameter("userId"),
                req.getParameter("password"),
                req.getParameter("name"),
                req.getParameter("email"));
        log.debug("Update User : {}", updateUser);
        user.update(updateUser);
        return "redirect:/";
    }

    @RequestMapping(value = "/users/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        return "redirect:/";
    }
}
