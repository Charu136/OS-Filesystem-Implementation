package com.vptr.sim;

public class VPTREntry {
	int logicalAddress;
	int pageNumber;
	int offset;
	int frameNumber;
	int sectorNumber;
	int isValid;

	public VPTREntry(int logicalAddress, int pageNumber, int offset, int frameNumber, int sectorNumber,
			int isValid) {
		this.logicalAddress = logicalAddress;
		this.pageNumber = pageNumber;
		this.offset = offset;
		this.frameNumber = frameNumber;
		this.sectorNumber = sectorNumber;
		this.isValid = isValid;
	}

}
