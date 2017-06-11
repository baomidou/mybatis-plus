package ${package.Controller};

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
#if(${superControllerClassPackage})
import ${superControllerClassPackage};
#end

/**
 * <p>
 * $!{table.comment} 前端控制器
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Controller
@RequestMapping("#if(${package.ModuleName})/${package.ModuleName}#end/${table.entityPath}")
#if(${superControllerClass})
public class ${table.controllerName} extends ${superControllerClass} {
#else
public class ${table.controllerName} {
#end
	
}
