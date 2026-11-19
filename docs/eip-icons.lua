-- PDF only: a <br> in a table cell becomes a line break, and EIP icons
-- are top-aligned so the text in the other cells starts level with them.
function RawInline(el)
  if el.format == "html" and el.text:match("^<br%s*/?>$") then
    return pandoc.LineBreak()
  end
end

function Image(el)
  if FORMAT:match("latex") and el.src:match("^img/eip/") then
    return pandoc.RawInline("latex", "\\adjustbox{valign=t}{\\includegraphics[width=1.7cm]{" .. el.src .. "}}")
  end
end
