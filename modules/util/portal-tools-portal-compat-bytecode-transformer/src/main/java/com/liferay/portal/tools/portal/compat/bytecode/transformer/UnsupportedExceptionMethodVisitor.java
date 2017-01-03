/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.tools.portal.compat.bytecode.transformer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author Tong Wang
 */
public class UnsupportedExceptionMethodVisitor extends MethodVisitor {

	public UnsupportedExceptionMethodVisitor(
		MethodVisitor methodVisitor, int argumentsSize) {

		super(Opcodes.ASM5, null);

		_methodVisitor = methodVisitor;
		_argumentsSize = argumentsSize;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return _methodVisitor.visitAnnotation(desc, visible);
	}

	@Override
	public void visitCode() {
		_methodVisitor.visitCode();

		_methodVisitor.visitTypeInsn(
			Opcodes.NEW,
			Type.getInternalName(UnsupportedOperationException.class));

		_methodVisitor.visitInsn(Opcodes.DUP);

		_methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			Type.getInternalName(UnsupportedOperationException.class), "<init>",
			"()V", false);

		_methodVisitor.visitInsn(Opcodes.ATHROW);

		_methodVisitor.visitMaxs(0, 0);

		_methodVisitor.visitEnd();
	}

	@Override
	public void visitLocalVariable(
		String name, String desc, String signature, Label start, Label end,
		int index) {

		if (index < _argumentsSize) {
			_methodVisitor.visitLocalVariable(
				name, desc, signature, start, end, index);
		}
	}

	private final int _argumentsSize;
	private final MethodVisitor _methodVisitor;

}