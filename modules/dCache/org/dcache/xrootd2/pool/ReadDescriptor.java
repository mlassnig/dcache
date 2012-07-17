package org.dcache.xrootd2.pool;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;

import org.dcache.xrootd2.protocol.messages.ReadRequest;
import org.dcache.xrootd2.protocol.messages.WriteRequest;
import org.dcache.xrootd2.protocol.messages.SyncRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates an open file for reading in the xrootd data server.
 */
public class ReadDescriptor implements FileDescriptor
{
    private final static Logger _log =
        LoggerFactory.getLogger(ReadDescriptor.class);

    /**
     * Update mover meta-information
     */
    private XrootdProtocol_3 _mover;

    public ReadDescriptor(XrootdProtocol_3 mover)
    {
        _mover = mover;
    }

    @Override
    public void close()
    {
        _mover.close(this);
    }

    @Override
    public Reader read(ReadRequest msg)
    {
        return new RegularReader(msg.getStreamID(),
                                 msg.getReadOffset(), msg.bytesToRead(),
                                 this);
    }

    @Override
    public void sync(SyncRequest msg)
    {
        _mover.updateLastTransferred();

        /* As this is a read only file, there is no reason to sync
         * anything.
         */
    }

    @Override
    public void write(WriteRequest msg)
        throws IOException
    {
        throw new IOException("File is read only");
    }

    @Override
    public FileChannel getChannel() throws ClosedChannelException
    {
        RandomAccessFile file = _mover.getFile();
        return file.getChannel();
    }

    public XrootdProtocol_3 getMover()
    {
        return _mover;
    }
}

