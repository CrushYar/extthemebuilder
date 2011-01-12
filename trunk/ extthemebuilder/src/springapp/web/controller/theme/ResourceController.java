package springapp.web.controller.theme;

import cesium.factory.ResourcesLoaderFactory;
import cesium.holder.CSSHolderImpl;
import cesium.holder.GIFHolderImpl;
import cesium.holder.PNGHolderImpl;
import cesium.holder.ResourcesHolder;
import cesium.loader.ResourcesLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import springapp.constants.ApplicationConstants;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * @project: Theme Builder for ExtJS 3.x
 * @Description:
 * @license: LGPL_v3
 * @author: Sergey Chentsov (extjs id: iv_ekker)
 * @mailto: sergchentsov@gmail.com
 * @version: 1.0.0
 * @Date: 27.08.2009
 * @Time: 13:09:09
 */
public class ResourceController implements Controller {
    protected final Log logger = LogFactory.getLog(getClass());

    private ResourcesLoaderFactory loaderFactory ;

    public ModelAndView handleRequest(HttpServletRequest httpServletRequest,
                                      HttpServletResponse httpServletResponse)
            throws Exception {
        String thisControllerUrl = httpServletResponse.encodeURL(httpServletRequest.getRequestURI());
        HttpSession session = httpServletRequest.getSession();

        ServletContext servletContext = session.getServletContext();
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        //String resourcesPath = (String)context.getBean("resourcesPath");
        
        ResourcesHolder currentSchema =(ResourcesHolder) session.getAttribute(
                ApplicationConstants.CURRENT_SCHEMA_ATTRIBUTE_NAME);

        String resourcesPath = currentSchema.getResourcesPath();
        String resourcePath = httpServletRequest.getParameter("resourcePath");
        ResourcesHolder resource = currentSchema.findResourceByPath(resourcePath);

        ResourcesLoader loader = loaderFactory.getResourcesLoader(resource, context);
        OutputStream outputStream = loader.outForWeb(resource, thisControllerUrl, resourcesPath);

        if (outputStream instanceof ByteArrayOutputStream){
            ByteArrayOutputStream byteArrayOutputStream = (ByteArrayOutputStream) outputStream;
            if (resource instanceof CSSHolderImpl)
                httpServletResponse.setContentType("text/css");
            else if (resource instanceof GIFHolderImpl)
                httpServletResponse.setContentType("image/gif");
            else if (resource instanceof PNGHolderImpl)
                httpServletResponse.setContentType("image/png");

            httpServletResponse.setHeader("Pragma", "no-cache");
            httpServletResponse.setHeader("Cache-Control", "no-cache");
            httpServletResponse.setHeader("Expires", "-1");
            httpServletResponse.setContentLength(byteArrayOutputStream.size());

            ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
            byteArrayOutputStream.writeTo(servletOutputStream);
            servletOutputStream.flush();
            servletOutputStream.close();
        }
        logger.info("ResourceController ! ");

        return null;
    }

    public ResourcesLoaderFactory getLoaderFactory() {
        return loaderFactory;
    }

    public void setLoaderFactory(ResourcesLoaderFactory loaderFactory) {
        this.loaderFactory = loaderFactory;
    }
}