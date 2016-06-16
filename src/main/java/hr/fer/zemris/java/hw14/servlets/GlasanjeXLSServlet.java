package hr.fer.zemris.java.hw14.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import hr.fer.zemris.java.hw14.VotingUtil.Info;
import hr.fer.zemris.java.tecaj_14.dao.DAOProvider;

/**
 * This servlet creates an XLS file with voting results. Since the voting
 * results are generated dynamically, this file keeps track of the current
 * results and is generated just in time it is requested.
 *
 * @author Mario Bobic
 */
@WebServlet(name="glasanje-xls", urlPatterns={"/glasanje-xls"})
public class GlasanjeXLSServlet extends HttpServlet {
	/** Serialization UID. */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/octet-stream"); // application/vnd.ms-excel
		resp.setHeader("Content-Disposition", "attachment; filename=\"vote_results.xls\"");
		
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
		
		HSSFWorkbook xls = createXLSFile(infoList);
		xls.write(resp.getOutputStream());
		xls.close();
	}
	
	/**
	 * Creates an instance of {@code HSSFWorkbook} with one sheet, having
	 * <tt>4</tt> columns and <tt>bandList.size()</tt> rows.
	 * <p>
	 * The columns are generated in the following way:
	 * <ol>
	 * <li>ID (unique ID of the information object)
	 * <li>Name (name of the information object)
	 * <li>Votes (number of votes the information object has gained)
	 * <li>Link of the information object
	 * </ol>
	 * 
	 * @param infoList info list
	 * @return a <tt>HSSFWorkbook</tt> object
	 */
	public HSSFWorkbook createXLSFile(List<Info> infoList) {
		HSSFWorkbook hwb = new HSSFWorkbook();

		HSSFSheet page = hwb.createSheet("results");
		
		HSSFRow header = page.createRow(0);
		header.createCell(0).setCellValue("ID");
		header.createCell(1).setCellValue("Name");
		header.createCell(2).setCellValue("Votes");
		header.createCell(3).setCellValue("Link");
		
		for (int i = 0, n = infoList.size(); i < n; i++) {
			HSSFRow row = page.createRow(i+1);
			
			Info info = infoList.get(i);
			
			row.createCell(0).setCellValue(info.id);
			row.createCell(1).setCellValue(info.name);
			row.createCell(2).setCellValue(info.getVotes());
			row.createCell(3).setCellValue(info.link);
		}

		return hwb;
	}
}