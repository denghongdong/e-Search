package pf.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BufferedRandomAccessFile extends RandomAccessFile {

	private static int defaultBufferSize = 2048;
	protected byte buf[] = new byte[defaultBufferSize]; // 建立读缓存区
	long bufstartpos = -1;
	long bufendpos = -1;
	long bufusedsize = 0;

	public BufferedRandomAccessFile(String name, String mode)
			throws FileNotFoundException {
		super(name, mode);
		// TODO Auto-generated constructor stub
	}

	// byte read(long pos)：读取当前文件POS位置所在的字节
	// bufstartpos、bufendpos代表BUF映射在当前文件的首/尾偏移地址。
	// curpos指当前类文件指针的偏移地址。
	public byte read(long pos) throws IOException {
		if (pos < this.bufstartpos || pos > this.bufendpos) {
			this.seek(pos);
			if ((pos < this.bufstartpos) || (pos > this.bufendpos))
				throw new IOException();
		}
		return this.buf[(int) (pos - this.bufstartpos)];
	}

	/*
	 * // void flushbuf()：bufdirty为真，把buf[]中尚未写入磁盘的数据，写入磁盘。 private void
	 * flushbuf() throws IOException { if (this.bufdirty == true) { if
	 * (super.getFilePointer() != this.bufstartpos) {
	 * super.seek(this.bufstartpos); } super.write(this.buf, 0,
	 * this.bufusedsize); this.bufdirty = false; } }
	 */

	// void seek(long pos)：移动文件指针到pos位置，并把buf[]映射填充至POS所在的文件块。
	public void seek(long pos) throws IOException {
		if ((pos < this.bufstartpos) || (pos > this.bufendpos)) {
			// seek pos not in buf
			// this.flushbuf();
			if ((pos >= 0) && (pos <= this.length()) && (this.length() != 0)) { // seek
																				// pos
																				// in
																				// file
																				// (file
				// length > 0)
				this.bufstartpos = pos;
				this.bufusedsize = this.fillbuf();
			} else if (((pos == 0) && (this.length() == 0))
					|| (pos == this.length() + 1)) { // seek pos is append pos
				this.bufstartpos = pos;
				this.bufusedsize = 0;
			}
			this.bufendpos = this.bufstartpos + this.bufusedsize - 1;
		}
	}

	// int fillbuf()：根据bufstartpos，填充buf[]。
	private int fillbuf() throws IOException {
		super.seek(this.bufstartpos);
		// this.bufdirty = false;
		return super.read(this.buf);
	}
}
