package hr.fer.zemris.java.hw14.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import hr.fer.zemris.java.hw14.VotingUtil.Poll;
import hr.fer.zemris.java.tecaj_14.dao.DAOProvider;

/**
 * This servlet represents a voting start-page which obtains the polls from
 * voting database and forwards the request to <tt>/WEB-INF/pages/index.jsp</tt>.
 *
 * @author Mario Bobic
 */
@WebServlet(name="index", urlPatterns={"/index.html", "/"})
public class IndexServlet extends HttpServlet {
	/** Serialization UID. */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Poll> pollList = DAOProvider.getDao().getPollList();
		
		req.setAttribute("pollList", pollList);
		req.getRequestDispatcher("/WEB-INF/pages/index.jsp").forward(req, resp);
	}
	
}
