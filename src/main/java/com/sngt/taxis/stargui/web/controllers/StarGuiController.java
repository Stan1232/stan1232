package com.sngt.taxis.stargui.web.controllers;

import com.sngt.taxis.stargui.web.dao.DiscussionDAOInterface;
import com.sngt.taxis.stargui.web.dao.EventDAOInterface;
import com.sngt.taxis.stargui.web.dao.MailDAOInterface;
import com.sngt.taxis.stargui.web.dao.UserDAOInterface;
import com.sngt.taxis.stargui.web.model.Discussion;
import com.sngt.taxis.stargui.web.model.Event;
import com.sngt.taxis.stargui.web.model.Mail;
import com.sngt.taxis.stargui.web.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class StarGuiController {

    public static final Logger LOGGER = LoggerFactory.getLogger(StarGuiController.class);


    @Autowired
    UserDAOInterface userRepository;
    @Autowired
    EventDAOInterface eventRepository;
    @Autowired
    DiscussionDAOInterface disccussionRepository;
    @Autowired
    MailDAOInterface mailRepository;

    /****************
     * TEST
     */

    @RequestMapping(method = RequestMethod.GET, value = "/test")
    public String test() {

        String msg = "MAIL 1";
        Integer user = 3;
        Integer eventId = 1;
        LOGGER.info("Send a mail:" + msg);

        //Récupération de l'utilisateur
        User user1 = userRepository.findByUserId(user);

        //Sauvegarde du mail
        Mail mail = new Mail(msg, user1);
        mailRepository.save(mail);

        //Sauvegarde de la discussion
        Discussion discussion = new Discussion(mail, user1);
        disccussionRepository.save(discussion);

        //Sauvegarde de l'event
        Event eventById = eventRepository.findByEventId(1);
        eventById.setDiscussion(discussion);
        eventRepository.save(eventById);

        LOGGER.info("Discussion savedID:" + discussion.getDiscId());
        LOGGER.info("Discussion savedID:" + eventById.getEventId() + " discussion:" + eventById.getDiscussion().getDiscId() + " Discussion:" + eventById.getDiscussion().toString());

        return "index";
    }

    /*****************
     * DASHBOARD
     **************************/
    @RequestMapping(method = RequestMethod.POST, value = "/dashboard")
    public ModelAndView dashboard(String mail, String pass, HttpServletRequest request) {
        LOGGER.info("Click Identify Login Mail:" + mail + " Pass:" + pass);

        /* Création ou récupération de la session */
        HttpSession session = request.getSession();
        LOGGER.info("Création de la session");

        User byMail = userRepository.findByMail(mail);
        ModelAndView modelView;
        if (null != byMail) {
            LOGGER.info(byMail.toString());
            if (pass.equals(byMail.getPass())) {
                modelView = new ModelAndView("MemberDisplay");
                modelView.addObject("user", byMail);
                modelView.addObject("edit", "yes");
                /* Mise en session d'une chaîne de caractères */
                session.setAttribute("userId", byMail.getUserId());
                session.setAttribute("user", byMail);
                LOGGER.info("Insertion dans la session de la valeur:" + byMail.getUserId());
            } else {
                modelView = new ModelAndView("index");
            }
        } else {
            modelView = new ModelAndView("index");
        }
        return modelView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/dashboard")
    public ModelAndView dashboardGET(HttpServletRequest request) {
    /* Création ou récupération de la session */
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        LOGGER.info("Récupération de la session de la valeur de session : " + userId);
        User userById = userRepository.findByUserId(userId);

        if (null != userById) {
            LOGGER.info(userById.toString());
            ModelAndView modelView = new ModelAndView("MemberDisplay");
            modelView.addObject("user", userById);
            modelView.addObject("edit", "yes");
            modelView.addObject("birthDate", calculAge(userById.getBirthDate()));
            return modelView;
        }
        return null;
    }

    //TODO a mettre dans un Singleton de type Util.
    private Object calculAge(String birthDate) {

        Calendar curr = Calendar.getInstance();
        Calendar birth = Calendar.getInstance();
        try {
            birth.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(birthDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LOGGER.info("Current Year:" + curr.get(Calendar.YEAR) + " Birth Year:" + birth.get(Calendar.YEAR));
        int yeardiff = curr.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        curr.add(Calendar.YEAR, -yeardiff);
        if (birth.after(curr)) {
            yeardiff = yeardiff - 1;
        }

        LOGGER.info("Entree:" + birthDate + " Sortie:" + yeardiff);

        return yeardiff;
    }

    /*****************
     * MEMBER
     **************************/
    @RequestMapping(method = RequestMethod.GET, value = "/member/register")
    public ModelAndView registerGet(HttpServletResponse response) {
        LOGGER.info("Click Register");
        ModelAndView modelView = new ModelAndView("MemberModify");
        modelView.addObject("register", "yes");
        User user = new User();
        modelView.addObject("user", user);
        return modelView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/member/modify")
    public ModelAndView registerAdvancedGet(HttpServletRequest request) {
        LOGGER.info("Click Register Advancedd");

        /* Récupération de l'objet depuis la session */
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        LOGGER.info("Récupération de la session de la valeur de session : " + userId);
        User userById = userRepository.findByUserId(userId);

        ModelAndView modelView = new ModelAndView("MemberModify");
        modelView.addObject("user", userById);
        LOGGER.info("Envoie de l'user avec la description : " + userById.getDescription());

        return modelView;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/member/modify")
    public ModelAndView memberModifyPost(@RequestParam("name") String name, @RequestParam("file") MultipartFile file, User userJSON, HttpServletRequest request) {

        LOGGER.info("Click save member modification userId:" + userJSON.getUserId() + "  Location:" + userJSON.getLocation());
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        LOGGER.info("Récupération de la session de la valeur de session : " + userId);

        ModelAndView modelView = new ModelAndView("MemberDisplay");
        //Vérification de non hack
        //if (userId == userJSON.getUserId()) {

        LOGGER.info("Meme user");
        User userDB = userRepository.findByUserId(userId);

        if (null != userDB) {
            //Si l'utilisateur existe déjà alors on merge avant de sauvagarder
            userDB.mergeModify(userJSON);
            userDB = userRepository.save(userDB);
            modelView.addObject("user", userDB);
            LOGGER.info("User updated " + userDB.getUserId());
        } else {
            //Sinon on save l'utilisateur récupéré
            userDB = userRepository.save(userJSON);
            modelView.addObject("user", userJSON);
            LOGGER.info("User created " + userJSON.getUserId());
        }

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(""+userDB.getUserId()+".jpg")));
                stream.write(bytes);
                stream.close();
                LOGGER.info("You successfully uploaded " + name + "!");
            } catch (Exception e) {
                LOGGER.info("You failed to upload " + name + " => " + e.getMessage());
            }
        } else {
            LOGGER.info("You failed to upload " + name + " because the file was empty.");
        }

        modelView.addObject("edit", "yes");

       /* } else {
            //TODO Gérer le hack
            LOGGER.error("Member modify retourne null");
            return null;
        }*/

        //SI save a fonctionné alors on retourne à l'index en disant OK
        //SINON on retourne à la meme pas en disant non OK et les champs qui posent problèmes.

        /* Récupération de l'objet depuis la session */
        return modelView;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/member/register")
    public ModelAndView registerPost(User u) {

        LOGGER.info("Click Register.");

        User userSaved = userRepository.save(u);

        if (userSaved == null)
            LOGGER.info("La sauvegarde de l'utilsateur n'a pas marché.");

        //SI save a fonctionné alors on retourne à l'index en disant OK
        //SINON on retourne à la meme page en disant non OK et les champs qui posent problèmes.

        ModelAndView modelView = new ModelAndView("MemberDisplay");
        modelView.addObject("user", u);
        modelView.addObject("response", "yes");

        return modelView;

    }

    @RequestMapping(method = RequestMethod.GET, value = "/member/display" + "/{login}")
    public ModelAndView memberProfileDisplay(@PathVariable String login) {
        LOGGER.info("Member Profil Display : GET of the member with nickname=" + login);

        User byLogin = userRepository.findByLogin(login);
        LOGGER.info(byLogin.toString());

        ModelAndView modelView = new ModelAndView("MemberDisplay");
        modelView.addObject("user", byLogin);
        modelView.addObject("birthDate", calculAge(byLogin.getBirthDate()));

        return modelView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/member/search")
    public String MemberSearchGet() {
        LOGGER.info("Click Membres Recherche : GET");
        return "MemberSearch";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/member/search")
    public ModelAndView MemberSearchPost(String login, String location) {
        LOGGER.info("Click Membres Recherche Search : POST. Login:" + login + " Location:" + location);

        List<User> listUserByLoginLike = userRepository.findByLoginLikeAndLocationLike("%" + login + "%", "%" + location + "%");

        ModelAndView modelView = new ModelAndView("MemberSearch");
        if (null != listUserByLoginLike)
            modelView.addObject("listeUser", listUserByLoginLike);

        return modelView;
    }

    /*********************
     * EVENT
     ****************/
    @RequestMapping(method = RequestMethod.GET, value = "/event/create")
    public String EventCreateGET() {
        LOGGER.info("Click Event Create : GET");
        return "EventCreate";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/event/create")
    public ModelAndView EventCreatePOST(Event event, HttpServletRequest request) {
        LOGGER.info("Click Event Create : POST Event:" + event.toString());

        event.setDateCreation(new SimpleDateFormat("dd/MM/yyy").format(new Date()));
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        event.setUserId(userId);

        Event eventSaved = eventRepository.save(event);

        return eventDisplayGet(eventSaved.getEventId(), request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/event/search")
    public String EventSearchGet() {
        LOGGER.info("Click Event Search : GET");
        return "EventSearch";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/event/search")
    public ModelAndView EventSearchPost(String location, String type) {
        LOGGER.info("Click Event Search : POST, location:" + location + " type:" + type);

        List<Event> listeEvent = eventRepository.findByTypeLikeAndLocationLike("%" + type + "%", "%" + location + "%");

        ModelAndView modelView = new ModelAndView("EventSearch");
        if (null != listeEvent)
            modelView.addObject("listeEvent", listeEvent);

        LOGGER.info("Click Event Search : POST, retourne " + listeEvent.size());
        return modelView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/event/display" + "/{eventId}")
    public ModelAndView eventDisplayGet(@PathVariable Integer eventId, HttpServletRequest request) {

        LOGGER.info("Event Display : GET of the event with eventId=" + eventId);
        Event eventById = eventRepository.findByEventId(eventId);
        LOGGER.info("Description de l'event : " + eventById.toString());

        ModelAndView modelView = new ModelAndView("EventDisplay");
        modelView.addObject("event", eventById);

        User userWhoCreate = userRepository.findByUserId(eventById.getUserId());
        modelView.addObject("userLogin", userWhoCreate.getLogin());

        Integer userIdView = (Integer) request.getSession().getAttribute("userId");
        if (userIdView != userWhoCreate.getUserId())
            //TODO gérer une liste de participant si cet utilisateur c'est déjà inscrit
            modelView.addObject("inscribirse", "true");

        if (null != eventById.getListUserEnroll())
            for (User u : eventById.getListUserEnroll())
                LOGGER.info("Utilisateur enrollé : " + u.getUserId());

        return modelView;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/event/inscribirse" + "/{eventId}")
    public ModelAndView eventInscribirPost(@PathVariable Integer eventId, HttpServletRequest request) {

        LOGGER.info("Event Display : GET of the event with eventName=" + eventId);
        Event eventById = eventRepository.findByEventId(eventId);
        LOGGER.info("Description de l'évent : " + eventById.toString());

        Integer userIdView = (Integer) request.getSession().getAttribute("userId");
        User userWhoEnroll = userRepository.findByUserId(userIdView);
        //TODO gérer une liste de participant
        eventById.setParticipant(userWhoEnroll);

        Event eventSaved = eventRepository.save(eventById);

        return eventDisplayGet(eventSaved.getEventId(), request);
    }


    /************************
     * MailBox
     */


    @RequestMapping(method = RequestMethod.GET, value = "/mail/mailbox")
    public ModelAndView eventMailBoxGET(HttpServletRequest request) {

        LOGGER.info("Enter in mailBox display");

        Integer userId = (Integer) request.getSession().getAttribute("userId");

        User user = userRepository.findByUserId(userId);

        List<Discussion> listDiscussion = user.getListeDiscussion();

        LOGGER.info("userId:" + userId + " Taille liste Discussion:" + listDiscussion.size());

        ModelAndView modelView = new ModelAndView("MailBox");
        modelView.addObject("listDiscussion", listDiscussion);

        return modelView;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/mail/send")
    public ModelAndView eventMailUserSendPOST(Integer userId2, String sujet, String msg, HttpServletRequest request) {

        LOGGER.info("Send a mail:" + msg);

        //Récupération de l'utilisateur
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        User user1 = userRepository.findByUserId(userId);
        User user2 = userRepository.findByUserId(userId2);

        //Création d'un message
        Mail mail = new Mail(msg, user1);
        mailRepository.save(mail);

        //Création d'une nouvelle discussion obligatoirement
        Discussion discussion = new Discussion(sujet, mail, user1, user2);
        disccussionRepository.save(discussion);
        user1.addDiscussion(discussion);
        userRepository.save(user1);
        user2.addDiscussion(discussion);
        userRepository.save(user2);


        return memberProfileDisplay(user1.getLogin());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/mail/send" + "/{discId}")
    public ModelAndView eventMailUserSendPOST(@PathVariable Integer discId, String msg, HttpServletRequest request) {

        LOGGER.info("Send a mail:" + msg);

        //Get discussion
        Discussion discussion = disccussionRepository.findByDiscId(discId);

        //Récupération de l'utilisateur de la session
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        User userSession = userRepository.findByUserId(userId);

        //Verify if the user is in the discussion
        if (discussion.getListeUser().contains(userSession)) {
            LOGGER.info("L'user qui répond est bien déjà dans la discussion");
        } else {
            LOGGER.info("L'user qui répond n'est pas dans la discussion");
        }

        //Get users of discussion
        User user1 = discussion.getListeUser().get(0);
        User user2 = discussion.getListeUser().get(1);

        //Création d'un message
        Mail mail = new Mail(msg, userSession);
        mailRepository.save(mail);

        //Add the mail to the discussion
        discussion.addMail(mail);
        disccussionRepository.save(discussion);

        return eventMailboxMailGET(discId, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/mail/event" + "/{eventId}")
    public ModelAndView eventMailEventSendPOST(@PathVariable Integer eventId, String msg, HttpServletRequest request) {

        LOGGER.info("Send a mail:" + msg);

        //Récupération de l'utilisateur
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        User user1 = userRepository.findByUserId(userId);

        //Création d'un message
        Mail mail = new Mail(msg, user1);
        mailRepository.save(mail);

        //récupération de l'évenement, pour ajouter mail à la discussion
        Event eventById = eventRepository.findByEventId(eventId);
        Discussion discussion = eventById.getDiscussion();
        if (discussion == null) {
            discussion = new Discussion(mail, user1);
        } else {
            discussion.addMail(mail);
        }
        disccussionRepository.save(discussion);
        eventById.setDiscussion(discussion);
        eventRepository.save(eventById);

        LOGGER.info("Discussion savedID:" + discussion.getDiscId());
        LOGGER.info("Discussion savedID:" + eventById.getEventId() + " discussion:" + eventById.getDiscussion().getDiscId() + " Discussion:" + eventById.getDiscussion().toString());

        return eventDisplayGet(eventById.getEventId(), request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/mailbox/mail" + "/{discId}")
    public ModelAndView eventMailboxMailGET(@PathVariable Integer discId, HttpServletRequest request) {

        LOGGER.info("Display a discussion:" + discId);

        //Get discussion
        Discussion discussion = disccussionRepository.findByDiscId(discId);

        //Verification that the user can access to the discussion
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        User user = userRepository.findByUserId(userId);
        if (discussion.getListeUser().contains(user))
            LOGGER.info("L'user est bien dans la liste de discussion.");
        else
            LOGGER.info("L'user n'est pas dans la liste et ne peut pas accéder à la discussion");

        ModelAndView modelView = new ModelAndView("MailboxMail");
        modelView.addObject("discussion", discussion);

        return modelView;
    }

}