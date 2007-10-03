package org.jlibrary.web.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.util.Text;
import org.apache.log4j.Logger;
import org.jlibrary.core.entities.Author;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.DocumentMetaData;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.entities.Note;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.Types;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.jcr.JCRSecurityService;
import org.jlibrary.core.profiles.LocalServerProfile;
import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.repository.exception.AuthorNotFoundException;
import org.jlibrary.web.freemarker.FreemarkerExporter;
import org.jlibrary.web.freemarker.RepositoryContext;
import org.jlibrary.web.services.TicketService;

/**
 * This servlet will forward create/update requests to admin JSF application
 * 
 * @author mpermar
 *
 */
@SuppressWarnings("serial")
public class JLibraryForwardServlet extends JLibraryServlet {

	private static Logger logger = Logger.getLogger(JLibraryForwardServlet.class);

	private ServerProfile profile = new LocalServerProfile();
	
	@Override
	public void init() throws ServletException {

		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		processRequest(req,resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		processRequest(req,resp);
	}

	private void processRequest(HttpServletRequest req, HttpServletResponse resp) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Received content upload request");
		}
		
		String method;
		try {
			method = getField(req, resp, "method");
		} catch (FieldNotFoundException e) {
			return;
		}
		
		if (method.equals("update")) {
			update(req,resp);
		} else if (method.equals("create")) {
			create(req,resp);
		} else if (method.equals("delete")) {
			delete(req,resp);
		} else if (method.equals("comment")) {
			addComment(req,resp);
		} else if (method.equals("login")) {
			login(req,resp);
		} else if (method.equals("logout")) {
			logout(req,resp);
		} else if (method.equals("updateform")) {
			updateform(req,resp);
		} else if (method.equals("createform")) {
			createform(req,resp);
		} else {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		return;
	}
	
	private void createform(HttpServletRequest req, HttpServletResponse resp) {
		
		String repositoryName;
		String id;
		String type;
		try {
			repositoryName = getField(req, resp, "repository");
			id = getField(req, resp, "id");
			type = getField(req, resp, "type");
		} catch (FieldNotFoundException e) {
			return;
		}
		
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
				
		try {
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);
			Node node = 
				repositoryService.findNode(ticket, id);

//			String templatesDirectory = 
//				"/software/apache-tomcat-6.0.13-pruebas/webapps/jlibrary/templates/terrafirma";
			RepositoryContext context = 
				new RepositoryContext(repository,getTemplate(),null);
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			
			exporter.setRootURL(getRootURL(req));
			exporter.setRepositoryURL(getRepositoryURL(req, repositoryName));
			
			exporter.initExportProcess(context);
			if (type.equals("directory")) {
				resp.getOutputStream().write(
					exporter.exportDirectory((Directory)node, context, "directory-create.ftl").getBytes());
			} else if (type.equals("document")) {
				resp.getOutputStream().write(
					exporter.exportDirectory((Directory)node, context, "document-create.ftl").getBytes());				
			} else if (type.equals("category")) {
				resp.getOutputStream().write(
						exporter.exportDirectory((Directory)node, context, "category-create.ftl").getBytes());
			}
			resp.getOutputStream().flush();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}		
	}
	
	private void updateform(HttpServletRequest req, HttpServletResponse resp) {
		
		String repositoryName;
		String id;
		String type;
		try {
			repositoryName = getField(req, resp, "repository");
			id = getField(req, resp, "id");
			type = getField(req, resp, "type");
		} catch (FieldNotFoundException e) {
			return;
		}
		
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
				
		try {
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);

//			String templatesDirectory = 
//				"/software/apache-tomcat-6.0.13-pruebas/webapps/jlibrary/templates/terrafirma";
			RepositoryContext context = 
				new RepositoryContext(repository,getTemplate(),null);
			context.setTicket(ticket);
			FreemarkerExporter exporter = new FreemarkerExporter();
			
			exporter.setRootURL(getRootURL(req));
			exporter.setRepositoryURL(getRepositoryURL(req, repositoryName));
			
			exporter.initExportProcess(context);
			
			if (type.equals("node")) {
				Node node = 
					repositoryService.findNode(ticket, id);
				if (node.isDirectory()) {
					resp.getOutputStream().write(
						exporter.exportDirectory((Directory)node, context, "directory-update.ftl").getBytes());
				} else if (node.isDocument()) {
					resp.getOutputStream().write(
							exporter.exportDocument((Document)node, context, "document-update.ftl").getBytes());				
				}
			} else if (type.equals("category")) {
				Category category = repositoryService.findCategoryById(ticket, id);
				resp.getOutputStream().write(
						exporter.exportCategory(category, context, "category-update.ftl").getBytes());				
			}
			resp.getOutputStream().flush();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}		
	}
	
	private void logout(HttpServletRequest req, HttpServletResponse resp) {

		String repositoryName;
		try {
			repositoryName = getField(req, resp, "repository");
		} catch (FieldNotFoundException e1) {
			return;
		}	
		
		// Remove ticket from user's session
		TicketService.getTicketService().removeTicket(req, repositoryName);
		
		String refererURL = req.getHeader("referer");
		try {
			resp.sendRedirect(resp.encodeRedirectURL(refererURL));
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}	
	
	private void login(HttpServletRequest req, HttpServletResponse resp) {

		String repositoryName;
		String username;
		String password;
		try {
			repositoryName = getField(req, resp, "repository");
			username = getField(req, resp, "username");
			password = getField(req, resp, "password");	
		} catch (FieldNotFoundException e) {
			return;
		}
		
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		//TODO: Refactor init/end methods to interface and remove this explicit cast
		JCRSecurityService securityService = 
			(JCRSecurityService)JLibraryServiceFactory.getInstance(profile).getSecurityService();
		
		Credentials credentials = new Credentials();
		if (username.equals(User.ADMIN_KEYNAME)) {
			username = User.ADMIN_NAME;
		}
		credentials.setUser(username);
		credentials.setPassword(password);
		try {
			Ticket userTicket = securityService.login(credentials, repositoryName);
			TicketService.getTicketService().putTicket(req, repositoryName, userTicket);
			String refererURL = req.getHeader("referer");
			resp.sendRedirect(resp.encodeRedirectURL(refererURL));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	private void update(HttpServletRequest req, HttpServletResponse resp) {

		String id;
		String repositoryName;
		String name;
		String description;
		String type;
		
		try {
			id = getField(req, resp, "id");
			repositoryName = getField(req, resp, "repository");
			name = getField(req, resp, "name");
			description = getField(req, resp, "description");	
			type = getField(req, resp, "type");
		} catch (FieldNotFoundException e) {
			return;
		}
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();

		try {
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);
			if (type.equals("node")) {
				Node node = 
					repositoryService.findNode(ticket, id);
	
				if (node.isDirectory()) {
					node.setName(name);
					node.setDescription(description);
					DirectoryProperties properties = ((Directory)node).dumpProperties();
					node = repositoryService.updateDirectory(ticket, properties);
					
					String url = getRepositoryURL(req, repositoryName);
					url+=node.getPath();
					resp.sendRedirect(resp.encodeRedirectURL(url));
				} else if (node.isDocument()) {
					node.setName(name);
					node.setDescription(description);
					DocumentProperties properties = ((Document)node).dumpProperties();
					node = repositoryService.updateDocument(ticket, properties);

					String content = req.getParameter("FCKEditor");
					repositoryService.updateContent(ticket, node.getId(), content.getBytes());
					
					String url = getRepositoryURL(req, repositoryName);
					url+=node.getPath();
					resp.sendRedirect(resp.encodeRedirectURL(url));
					
				}
			} else if (type.equals("category")) {
				Category category = repositoryService.findCategoryById(ticket, id);
				category.setName(name);
				category.setDescription(description);

				CategoryProperties properties = category.dumpProperties();
				category = repositoryService.updateCategory(ticket, id, properties);
				
				String url = getRepositoryURL(req, repositoryName);
				url+="/categories/"+category.getName();
				resp.sendRedirect(resp.encodeRedirectURL(url));	
			}

			resp.getOutputStream().flush();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void delete(HttpServletRequest req, HttpServletResponse resp) {

		String id;
		String repositoryName;
		String type;
		
		try {
			id = getField(req, resp, "id");
			repositoryName = getField(req, resp, "repository");
			type = getField(req, resp, "type");
		} catch (FieldNotFoundException e) {
			return;
		}
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();

		try {
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);
			if (type.equals("node")) {
				Node node = 
					repositoryService.findNode(ticket, id);
				Node parent = 
					repositoryService.findNode(ticket, node.getParent());
				if (node.isDirectory()) {
					repositoryService.removeDirectory(ticket, node.getId());
				} else if (node.isDocument()) {
					repositoryService.removeDocument(ticket, node.getId());
				}
				String url = getRepositoryURL(req, repositoryName);
				url+=parent.getPath();
				resp.sendRedirect(resp.encodeRedirectURL(url));
			} else if (type.equals("category")) {
				repositoryService.deleteCategory(ticket, id);
				
				String url = getRepositoryURL(req, repositoryName);
				resp.sendRedirect(resp.encodeRedirectURL(url));	
			}

			resp.getOutputStream().flush();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	
	private void create(HttpServletRequest req, HttpServletResponse resp) {

		String id = null;
		String repositoryName;
		String name;
		String description;
		String type;
		String keywords = null;
		
		try {
			type = getField(req, resp, "type");
			if (!type.equals("category")) {
				id = getField(req, resp, "id");
			}
			repositoryName = getField(req, resp, "repository");
			name = getField(req, resp, "name");
			description = getField(req, resp, "description");
			if (type.equals("document")) {
				keywords = getField(req, resp, "keywords");
			}
		} catch (FieldNotFoundException e) {
			return;
		}
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();

		try {
			Repository repository = repositoryService.findRepository(repositoryName, ticket);
			repository.setServerProfile(profile);

			if (type.equals("directory")) {
				Directory directory = new Directory();
				directory.setName(name);
				directory.setDescription(description);
				directory.setParent(id);
				directory.setPosition(new Integer(1));
				DirectoryProperties properties = directory.dumpProperties();
				directory = repositoryService.createDirectory(ticket, properties);
				
				String url = getRepositoryURL(req, repositoryName);
				url+=directory.getPath();
				resp.sendRedirect(resp.encodeRedirectURL(url));
			} else if (type.equals("document")) {
				String url = getRepositoryURL(req, repositoryName);
				
				Author author = null;
				try {
					author = repositoryService.findAuthorByName(ticket, ticket.getUser().getName());
				} catch (AuthorNotFoundException anfe) {
					author = Author.UNKNOWN;
				}
				Document document = new Document();
				document.setName(name);
				document.setDescription(description);
				document.setParent(id);
				document.setPosition(new Integer(1));
				document.setImportance(Node.IMPORTANCE_MEDIUM);
				document.setDate(new Date());
				document.setPath(Text.escape(name)+".html");
				document.setTypecode(Types.HTML_DOCUMENT);
				document.setRelations(new HashSet());
				document.addCategory(Category.UNKNOWN);
				
				DocumentMetaData metaData = new DocumentMetaData();
				metaData.setDate(new Date());
				metaData.setTitle(name);
				metaData.setKeywords(keywords);
				metaData.setUrl(url);
				metaData.setAuthor(author);
				document.setMetaData(metaData);
				DocumentProperties properties = document.dumpProperties();
				document = repositoryService.createDocument(ticket, properties);	
				
				String content = req.getParameter("FCKEditor");
				repositoryService.updateContent(ticket, document.getId(), content.getBytes());
				
				url+=document.getPath();
				resp.sendRedirect(resp.encodeRedirectURL(url));
			} else if (type.equals("category")) {
				Category category = new Category();
				category.setId(""); //TODO: This is a bug, is to make dumpProperties work propertly
				category.setName(name);
				category.setDescription(description);
				category.setParent(null);
				category.setRepository(repository.getId());
				CategoryProperties properties = category.dumpProperties();
				category = repositoryService.createCategory(ticket, properties);
				
				String url = getRepositoryURL(req, repositoryName);
				url+="/categories/"+category.getName();
				resp.sendRedirect(resp.encodeRedirectURL(url));				
			}

			resp.getOutputStream().flush();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void addComment(HttpServletRequest req, HttpServletResponse resp) {

		String id;
		String repositoryName;
		String text;
		
		try {
			id = getField(req, resp, "document");
			repositoryName = getField(req, resp, "repository");		
			text = getField(req, resp, "text");
		} catch (FieldNotFoundException e) {
			return;
		}
		Ticket ticket = TicketService.getTicketService().getTicket(req, repositoryName);
		RepositoryService repositoryService = 
			JLibraryServiceFactory.getInstance(profile).getRepositoryService();
		try {
			Document document = repositoryService.findDocument(ticket, id);
			Note note = new Note();
			note.setCreator(ticket.getUser().getId());
			note.setDate(new Date());
			note.setNode(document);
			note.setNote(processNote(text));
			document.addNote(note);
			repositoryService.updateDocument(ticket, document.dumpProperties());
			
			String refererURL = req.getHeader("referer"); 
			resp.sendRedirect(resp.encodeRedirectURL(refererURL));

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}	
	}
	
	private String processNote(String text) {

		return text.replaceAll("\n", "<br/>");
	}


	private String getRootURL(HttpServletRequest request) {

		return request.getScheme( ) + "://"
				+ request.getLocalAddr( )  + ":"
				+ request.getLocalPort( )
				+ request.getContextPath( );
	}

	private String getRepositoryURL(HttpServletRequest request, String repositoryName) {
		
		return getRootURL(request) + "/repositories/" + repositoryName;
	}
	
	private String getField(HttpServletRequest req, 
							HttpServletResponse resp, 
							String fieldName) throws FieldNotFoundException {
		
		String field = req.getParameter(fieldName);
		if (field == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invalid update request. Field '" + fieldName + "' not found.");
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				throw new FieldNotFoundException();
			}
		}
		return field;
	}
}
