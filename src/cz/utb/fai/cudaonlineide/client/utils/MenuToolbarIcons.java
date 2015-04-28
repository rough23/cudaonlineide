package cz.utb.fai.cudaonlineide.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Class provides access to image resources.
 * 
 * @author Belanec
 *
 */
public interface MenuToolbarIcons extends ClientBundle {
	
	public MenuToolbarIcons PROVIDER = GWT.create(MenuToolbarIcons.class);
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/tree/folder.png")
	ImageResource folder();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/tree/project.png")
	ImageResource project();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/tree/project-active.png")
	ImageResource projectActive();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/tree/workspace.png")
	ImageResource workspace();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/tree/c.png")
	ImageResource c();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/tree/cpp.png")
	ImageResource cpp();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/tree/cu.png")
	ImageResource cu();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/tree/cuh.png")
	ImageResource cuh();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/tree/h.png")
	ImageResource h();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/tree/other.png")
	ImageResource other();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/tree/res.png")
	ImageResource res();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/tree/txt.png")
	ImageResource txt();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/class.png")
	ImageResource outClass();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/cpp-keyword.png")
	ImageResource outKeyword();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/enum.png")
	ImageResource outEnum();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/enumerator.png")
	ImageResource outEnumerator();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/function_private.png")
	ImageResource outFunctionPrivate();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/function_protected.png")
	ImageResource outFunctionProtected();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/function_public.png")
	ImageResource outFunctionPublic();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/macro.png")
	ImageResource outMacro();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/member_private.png")
	ImageResource outMemberPrivate();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/member_protected.png")
	ImageResource outMemberProtected();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/member_public.png")
	ImageResource outMemberPublic();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/struct.png")
	ImageResource outStruct();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/typedef.png")
	ImageResource outTypedef();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/union.png")
	ImageResource outUnion();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/outline/word.png")
	ImageResource outWord();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/build.png")
	ImageResource menuBuild();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/close.png")
	ImageResource menuClose();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/delete.png")
	ImageResource menuDelete();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/download.png")
	ImageResource menuDownload();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/new.png")
	ImageResource menuNew();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/open.png")
	ImageResource menuOpen();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/properties.png")
	ImageResource menuProperties();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/run.png")
	ImageResource menuRun();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/save.png")
	ImageResource menuSave();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/upload.png")
	ImageResource menuUpload();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/cmake.png")
	ImageResource menuCmake();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/cmakelists.png")
	ImageResource menuCmakeLists();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/connection.png")
	ImageResource menuConnection();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/server.png")
	ImageResource menuServer();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/about.png")
	ImageResource menuAbout();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/tabsize.png")
	ImageResource menuTabSize();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/add.png")
	ImageResource menuAdd();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/gotoline.png")
	ImageResource menuGoToLine();

	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/insert.png")
	ImageResource menuInsert();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/logout.png")
	ImageResource menuLogout();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/highlight.png")
	ImageResource menuHighlight();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/menu/console.png")
	ImageResource menuConsole();
	
	@Source("cz/utb/fai/cudaonlineide/client/resources/logo/cuda.png")
	ImageResource logoCuda();
}
