package com.baomidou.framework.velocity.directive;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import com.baomidou.framework.velocity.directive.OverrideDirective.OverrideNodeWrapper;

public class SuperDirective extends Directive {

	@Override
	public String getName() {
		return "super";
	}


	@Override
	public int getType() {
		return LINE;
	}


	@Override
	public boolean render( InternalContextAdapter context, Writer writer, Node node )
		throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

		OverrideNodeWrapper current = (OverrideNodeWrapper) context.get(DirectiveUtil.OVERRIDE_CURRENT_NODE);
		if ( current == null ) {
			throw new ParseErrorException("#super direction must be child of override");
		}
		OverrideNodeWrapper parent = current.parentNode;
		if ( parent == null ) {
			throw new ParseErrorException("not found parent block for #super ");
		}
		return parent.render(context, writer);
	}

}
