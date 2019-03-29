package hr.fer.zemris.java.hw14.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

import hr.fer.zemris.java.hw14.VotingUtil.Info;
import hr.fer.zemris.java.tecaj_14.dao.DAOProvider;

/**
 * This servlet represents a voting pie chart that creates a PNG image of the
 * chart using the {@linkplain JFreeChart}. The chart is <strong>not</strong>
 * predefined as voting results are dynamically changed. The image is written
 * to the output stream as a HTTP servlet response.
 *
 * @author Mario Bobic
 */
@WebServlet(name="glasanje-grafika", urlPatterns={"/glasanje-grafika"})
public class GlasanjeGrafikaServlet extends HttpServlet {
    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("image/png");

        long pollID;
        try {
            pollID = Long.parseLong(req.getParameter("pollID"));
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Poll ID must be a valid integer!");
            req.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(req, resp);
            return;
        }
        List<Info> infoList = DAOProvider.getDao().getInfoList(pollID);

        // Create dataset
        PieDataset dataset = createDataset(infoList);
        // Create a chart based on the dataset
        JFreeChart chart = createChart(dataset, "Rezultati glasanja");

        // Convert to an image and encode it
        byte[] image = ChartUtilities.encodeAsPNG(chart.createBufferedImage(400, 300));
        resp.getOutputStream().write(image);
    }

    /**
     * Creates a simple dataset.
     *
     * @param infoList info list with vote results
     * @return pie dataset
     */
    private PieDataset createDataset(List<Info> infoList) {
        DefaultPieDataset result = new DefaultPieDataset();

        infoList.forEach(info -> {
            result.setValue(info.name, info.getVotes());
        });

        return result;
    }

    /**
     * Creates an instance of {@code JFreeChart} whose dataset is specified by
     * the {@code PieDataset}.
     *
     * @param dataset pie dataset
     * @param title chart title
     * @return an instance of JFreeChart
     */
    private JFreeChart createChart(PieDataset dataset, String title) {
        JFreeChart chart = ChartFactory.createPieChart3D(
                title,    // chart title
                dataset,// data
                true,    // include legend
                true,    // include tooltips
                false    // exclude urls
        );

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        return chart;
    }

}
