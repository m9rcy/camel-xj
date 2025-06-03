<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns="http://example.com"
                exclude-result-prefixes="ns">

    <!-- Parameter passed from Camel route -->
    <xsl:param name="parentId"/>

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
        <LOCHIERARCHY creationDateTime="0001-01-01T00:00:00"
                      baseLanguange="EN"
                      version="1">
            <LOCHIERARCHYSET>
                <LOCSYSTEM action="AddChange">
                    <SITEID>TPNZ</SITEID>
                    <SYSTEMID>OUTAGE</SYSTEMID>

                    <!-- Loop through each TPBLOCK with STATUS = 'COMMISSION' -->
                    <xsl:for-each select="//ns:TPBLOCK[ns:STATUS='COMMISSION']">
                        <LOCHIERARCHY action="AddChange">
                            <LOCATION>
                                <xsl:value-of select="ns:LOCATION"/>
                            </LOCATION>
                            <PARENT>
                                <xsl:value-of select="$parentId"/>
                            </PARENT>
                        </LOCHIERARCHY>
                    </xsl:for-each>

                </LOCSYSTEM>
            </LOCHIERARCHYSET>
        </LOCHIERARCHY>
    </xsl:template>

</xsl:stylesheet>
