package com.baomidou.mybatisplus.activerecord;

import com.baomidou.mybatisplus.activerecord.exception.UndefinedAssociationException;

import java.util.Map;

/**
 * 表之间的关联。
 * 
 * @since 1.0
 * @author redraiment
 */
public final class Association {
	private final Map<String, Association> relations;
	private final boolean onlyOne;
	private final boolean ancestor;

	private Association assoc;
	String target;
	String key;

	Association(Map<String, Association> relations, String name, boolean onlyOne, boolean ancestor) {
		this.relations = relations;
		this.onlyOne = onlyOne;
		this.ancestor = ancestor;

		this.target = name;
		this.key = name.concat("_id");
		this.assoc = null;
	}

	public boolean isOnlyOneResult() {
		return onlyOne;
	}

	public boolean isAncestor() {
		return ancestor;
	}

	public boolean isCross() {
		return assoc != null;
	}

	public Association by(String key) {
		this.key = key;
		return this;
	}

	public Association in(String table) {
		this.target = table;
		return this;
	}

	public Association through(String assoc) {
		assoc = DB.parseKeyParameter(assoc);
		if (relations.containsKey(assoc)) {
			this.assoc = relations.get(assoc);
		} else {
			throw new UndefinedAssociationException(assoc);
		}
		return this;
	}

	String assoc(String source, int id) {
		String template = isAncestor() ? "%1$s on %2$s.%3$s = %1$s.id" : "%1$s on %1$s.%3$s = %2$s.id";
		if (isCross()) {
			return String.format(template, assoc.target, target, key).concat(" join ").concat(assoc.assoc(source, id));
		} else {
			return String.format(template.concat(" and %1$s.id = %4$dialect"), source, target, key, id);
		}
	}
}
