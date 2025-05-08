<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xj="http://camel.apache.org/component/xj"
                exclude-result-prefixes="xj">

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/object">
        <Link creationDateTime="0001-01-01T00:00:00" baseLanguange="EN" version="1">

            <RequestSet>

                <!-- Array-style add -->
                <xsl:for-each select="object[
                    object[@xj:name='op' and text()='add']
                    and object[@xj:name='path' and text()='/workOrderNumbers']
                ]/object[@xj:name='value']/object">
                    <LinkOR action="AddChange">
                        <ORNUM><xsl:value-of select="."/></ORNUM>
                    </LinkOR>
                </xsl:for-each>

                <!-- Indexed-style add -->
                <xsl:for-each select="object[
                    object[@xj:name='op' and text()='add']
                    and object[@xj:name='path' and starts-with(text(), '/workOrderNumbers/')]
                    and object[@xj:name='value']
                ]">
                    <LinkOR action="AddChange">
                        <ORNUM><xsl:value-of select="object[@xj:name='value']"/></ORNUM>
                    </LinkOR>
                </xsl:for-each>

                <!-- Array-style remove -->
                <xsl:for-each select="object[
                    object[@xj:name='op' and text()='remove']
                    and object[@xj:name='path' and text()='/workOrderNumbers']
                ]/object[@xj:name='value']/object">
                    <LinkOR action="Delete">
                        <ORNUM><xsl:value-of select="."/></ORNUM>
                    </LinkOR>
                </xsl:for-each>

                <!-- Indexed-style remove -->
                <xsl:for-each select="object[
                    object[@xj:name='op' and text()='remove']
                    and object[@xj:name='path' and starts-with(text(), '/workOrderNumbers/')]
                    and object[@xj:name='value']
                ]">
                    <LinkOR action="Delete">
                        <ORNUM><xsl:value-of select="object[@xj:name='value']"/></ORNUM>
                    </LinkOR>
                </xsl:for-each>

            </RequestSet>

        </Link>
    </xsl:template>
</xsl:stylesheet>
