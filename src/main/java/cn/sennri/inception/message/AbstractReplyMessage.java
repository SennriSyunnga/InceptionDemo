package cn.sennri.inception.message;

/**
 * 回复类消息;
 * 携带对方的id
 */
public abstract class AbstractReplyMessage<T> extends AbstractMessage {
    /**
     * 回复对象的id
     */
    protected long replyId;

    public boolean isFailed() {
        return failed;
    }

    protected boolean failed;

    public long getReplyId() {
        return replyId;
    }

    protected T reply;

    public T getReply() {
        return reply;
    }

    public void setReplyId(long replyId) {
        this.replyId = replyId;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public void setReply(T reply) {
        this.reply = reply;
    }
}
