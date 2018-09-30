package com.everis.fallas.operacionales.workitem.framework;

import com.ibm.team.links.common.IReference;
import com.ibm.team.links.common.registry.IEndPointDescriptor;

public class ReferenceData {

	private IEndPointDescriptor endpoint = null;
	private IReference reference = null;

	public ReferenceData(IEndPointDescriptor endPoint, IReference reference) {
		this.endpoint = endPoint;
		this.reference = reference;
	}

	public IEndPointDescriptor getEndPointDescriptor() {
		return this.endpoint;
	}

	public IReference getReference() {
		return this.reference;
	}
}
