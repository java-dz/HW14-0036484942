package hr.fer.zemris.java.hw14.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.zemris.java.tecaj_14.dao.DAOProvider;

/**
 * This servlet represents a vote action. Vote with the <tt>id</tt> specified by
 * a parameter from the user will be increased by one, updating the database.
 *
 * @author Mario Bobic
 */
@WebServlet(name="glasanje-glasaj", urlPatterns={"/glasanje-glasaj"})
public class GlasanjeGlasajServlet extends HttpServlet {
	/** Serialization UID. */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		long pollID;
		long voteID;
		try {
			pollID = Long.parseLong(req.getParameter("pollID"));
			voteID = Long.parseLong(req.getParameter("id"));
		} catch (NumberFormatException e) {
			req.setAttribute("error", "ID must be a valid integer!");
			req.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(req, resp);
			return;
		}
		
		DAOProvider.getDao().vote(voteID);
		
		resp.sendRedirect(req.getContextPath() + "/glasanje-rezultati?pollID="+pollID);
	}
	
}
