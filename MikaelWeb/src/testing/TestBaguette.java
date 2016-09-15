package testing;

import hus.BatmanRemote;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TestBaguette
 */
@WebServlet("/TestBaguette")
public class TestBaguette extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@EJB hus.BatmanRemote main;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestBaguette() {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if ("getVolum".equals(request.getQueryString()))
		{
			response.setHeader("Expires", "0");
			response.getOutputStream().println("Volum: " + main.getVolum());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("kilde") != null) {
			main.forsterkerNesteKilde();
			RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
			dispatcher.forward(request, response);
		}
		else {
			response.getOutputStream().println("Hello there   bb00000bs ");
			main.volumOpp();	
		}
	}

}
