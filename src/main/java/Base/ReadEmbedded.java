package Base;

public class ReadEmbedded {
    private String _text = "";
    private Boolean _isEmbedded;

    public String getText() {
        return _text;
    }

    public void setText(String _text) {
        this._text = _text;
    }

    public Boolean getEmbedded() {
        return _isEmbedded;
    }

    public void setEmbedded(Boolean embedded) {
        _isEmbedded = embedded;
    }
}
