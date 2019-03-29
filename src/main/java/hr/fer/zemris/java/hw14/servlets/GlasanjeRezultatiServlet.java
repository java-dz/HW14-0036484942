package hr.fer.zemris.java.hw14.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.zemris.java.hw14.VotingUtil.Info;
import hr.fer.zemris.java.tecaj_14.dao.DAOProvider;

/**
 * This servlet represents a voting results page which obtains an info list with
 * vote results from a database. The obtained info list is then processed to
 * leave out only the winners that will be used by the
 * <tt>/WEB-INF/pages/votingResults.jsp</tt> file.
 *
 * @author Mario Bobic
 */
@WebServlet(name="glasanje-rezultati", urlPatterns={"/glasanje-rezultati"})
public class GlasanjeRezultatiServlet extends HttpServlet {
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


        List<Info> infoList = DAOProvider.getDao().getInfoList(pollID);
        infoList.sort(Info.BY_VOTES);

        List<Info> winners = getWinners(infoList);

        req.setAttribute("pollID", pollID);
        req.setAttribute("infoList", infoList);
        req.setAttribute("winners", winners);
        req.getRequestDispatcher("/WEB-INF/pages/votingResults.jsp").forward(req, resp);
    }

    /**
     * Returns a <tt>List</tt> of {@linkplain Info} objects containing only
     * winners of the pole. The maximum number of votes an information object
     * has is calculated based on the votes in <tt>infoList</tt> list, which
     * <strong>must</strong> be sorted before passing it to this method.
     *
     * @param infoList info list, <strong>must</strong> be sorted
     * @return a list containing winners of the pole
     */
    private static List<Info> getWinners(List<Info> infoList) {
        long maxVotes = infoList.isEmpty() ? 0L : infoList.get(0).getVotes();

        List<Info> winners = new ArrayList<>();
        winners.addAll(infoList);
        winners.removeIf(bandInfo -> bandInfo.getVotes() < maxVotes);

        return winners;
    }

}
