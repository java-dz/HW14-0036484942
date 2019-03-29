package hr.fer.zemris.java.hw14.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.zemris.java.hw14.VotingUtil.Info;
import hr.fer.zemris.java.hw14.VotingUtil.Poll;
import hr.fer.zemris.java.tecaj_14.dao.DAOProvider;

/**
 * This servlet represents a voting on the specified poll ID. The poll options
 * are obtained from the poll ID and a list is forwarded to the
 * <tt>/WEB-INF/pages/vote.jsp</tt>.
 *
 * @author Mario Bobic
 */
@WebServlet(name="glasanje", urlPatterns={"/glasanje"})
public class GlasanjeServlet extends HttpServlet {
    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long pollID;
        try {
            pollID = Long.parseLong(req.getParameter("pollID"));
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Poll ID must be a valid integer!");
            req.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(req, resp);
            return;
        }

        Poll poll = DAOProvider.getDao().getPoll(pollID);
        List<Info> infoList = DAOProvider.getDao().getInfoList(pollID);

        req.setAttribute("poll", poll);
        req.setAttribute("infoList", infoList);
        req.getRequestDispatcher("/WEB-INF/pages/vote.jsp").forward(req, resp);
    }

}
