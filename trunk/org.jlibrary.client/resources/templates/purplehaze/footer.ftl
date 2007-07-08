<#macro footer>

    <div><div id="footer">  <!-- NB: outer <div> required for correct rendering in IE -->
      <a class="footerImg" href="http://validator.w3.org/check/referer">
        Valid XHTML 1.0 Strict</a>
      <a class="footerImg" href="http://jigsaw.w3.org/css-validator/validator">
        Valid CSS</a>

      <div>
        <strong>Author: </strong>
        ${user}
      </div>

      <div>
        <strong>URI: </strong>
        <span class="footerCol2"><A href="http://jlibrary.sourceforge.net">http://jlibrary.sourceforge.net</A></span>
      </div>

      <div>
        <strong>Modified: </strong>
        <span class="footerCol2">${date?datetime?string.long}</span>
      </div>
    </div></div>
</#macro>
