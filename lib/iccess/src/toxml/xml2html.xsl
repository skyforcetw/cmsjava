<?xml version="1.0" encoding="ISO-8859-1" standalone="yes"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" encoding="UTF-8" />

	<xsl:template match="/ICCProfile">
		<html>
		<head><title>ICC Profile - "<xsl:value-of select="@name"/>"</title></head>
		<body>
		<H1>ICC Profile - <xsl:value-of select="@name"/></H1>
		<xsl:apply-templates />
		<br/><hr/>
		<center><font size="-1">Produced by iccess</font></center>
		</body>
		</html>
	</xsl:template>

	<xsl:template match="header">
		<h2>The Header</h2>
		<table border="1" width="100%" bgcolor="#FFFF99">
		<xsl:apply-templates />
		</table>
	</xsl:template>
	
	<xsl:template match="version">
		<tr>
		<td>Version</td><td><xsl:value-of select="@major"/>.<xsl:value-of select="@minor"/>.<xsl:value-of select="@build"/></td>
		</tr>
	</xsl:template>
	
	<xsl:template match="dateTimeNumber">
		<tr>
		<td>DateTime</td>
		<td><xsl:value-of select="year/@value"/>-<xsl:value-of select="month/@value"/>-<xsl:value-of select="day/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="hour/@value"/>:<xsl:value-of select="minute/@value"/>:<xsl:value-of select="second/@value"/></td>
		</tr>
	</xsl:template>
	
	<xsl:template match="profileFlag">
		<tr>
		<td>Profile Flag</td><td><xsl:apply-templates select="flag"/></td>
		</tr>
	</xsl:template>
	<xsl:template match="flag">
		<xsl:value-of select="@name"/>: <xsl:value-of select="@value"/><br/>
	</xsl:template>
	
	<xsl:template match="deviceAttribute">
		<tr>
		<td>Device Attributes</td>
		<td><xsl:apply-templates select="field"/></td>
		</tr>
	</xsl:template>
	<xsl:template match="field">
		Bit <xsl:value-of select="@bit"/>:<xsl:value-of select="."/><br/>
	</xsl:template>

	<xsl:template match="XYZNumber">
		<tr>
		<td><xsl:value-of select="@name"/>[XYZNumber]</td>
		<td><xsl:for-each select="s15Fixed16Number">
			<xsl:value-of select="@name"/>:<xsl:value-of select="."/><br/>
		</xsl:for-each></td>
		</tr>
	</xsl:template>
	
	<xsl:template match="tagtable">
		<h2>The Tag Table</h2>
		<table border="1" width="100%" bgcolor="#CCFFFF">
		<tr bgcolor="#33CCFF"><th>#</th><th>Tag</th><th>Offset</th><th>Size</th></tr>
		<xsl:for-each select="entry">
			<tr><td><xsl:value-of select="@name"/></td>
			<td><xsl:value-of select="tag"/> (<xsl:call-template name="get-meaning"><xsl:with-param name="tag"><xsl:value-of select="tag"/></xsl:with-param></xsl:call-template>)</td>
			<td><xsl:value-of select="offset"/></td><td><xsl:value-of select="size"/></td></tr>
		</xsl:for-each>
		</table>
	</xsl:template>
	
	<xsl:template name="find-tag">
		<xsl:param name="number"/>
		<xsl:value-of select="/ICCProfile/tagtable/entry[@name=$number]/tag"/>
	</xsl:template>

	<xsl:template name="get-meaning">
		<xsl:param name="tag"/>
		<xsl:choose>
		<xsl:when test="$tag='cprt'">CopyrightTag</xsl:when>
		<xsl:when test="$tag='A2B0'">AToB0Tag</xsl:when>
		<xsl:when test="$tag='A2B1'">AToB1Tag</xsl:when>
		<xsl:when test="$tag='A2B2'">AToB2Tag</xsl:when>
		<xsl:when test="$tag='bXYZ'">blueMatrixColumnTag</xsl:when>
		<xsl:when test="$tag='bTRC'">blueTRCTag</xsl:when>
		<xsl:when test="$tag='B2A0'">BToA0Tag</xsl:when>
		<xsl:when test="$tag='B2A1'">BToA1Tag</xsl:when>
		<xsl:when test="$tag='B2A2'">BToA2Tag</xsl:when>
		<xsl:when test="$tag='calt'">calibrationDateTimeTag</xsl:when>
		<xsl:when test="$tag='targ'">charTargetTag</xsl:when>
		<xsl:when test="$tag='chad'">chromaticAdaptationTag</xsl:when>
		<xsl:when test="$tag='chrm'">chromaticityTag</xsl:when>
		<xsl:when test="$tag='clro'">colorantOrderTag</xsl:when>
		<xsl:when test="$tag='clrt'">colorantTableTag</xsl:when>
		<xsl:when test="$tag='clot'">colorantTableOutTag</xsl:when>
		<xsl:when test="$tag='dmnd'">deviceManufacturerDescription</xsl:when>
		<xsl:when test="$tag='dmdd'">deviceModelDescription</xsl:when>
		<xsl:when test="$tag='gamt'">gamutTag</xsl:when>
		<xsl:when test="$tag='kTRC'">grayTRCTag</xsl:when>
		<xsl:when test="$tag='gXYZ'">greenMatrixColumnTag</xsl:when>
		<xsl:when test="$tag='gTRC'">greenTRCTag</xsl:when>
		<xsl:when test="$tag='lumi'">luminanceTag</xsl:when>
		<xsl:when test="$tag='meas'">measurementTag</xsl:when>
		<xsl:when test="$tag='bkpt'">mediaBlackPointTag</xsl:when>
		<xsl:when test="$tag='wtpt'">mediaWhitePointTag</xsl:when>
		<xsl:when test="$tag='ncl2'">namedColor2Tag</xsl:when>
		<xsl:when test="$tag='resp'">outputResponseTag</xsl:when>
		<xsl:when test="$tag='pre0'">preview0Tag</xsl:when>
		<xsl:when test="$tag='pre1'">preview1Tag</xsl:when>
		<xsl:when test="$tag='pre2'">preview2Tag</xsl:when>
		<xsl:when test="$tag='desc'">profileDescriptionTag</xsl:when>
		<xsl:when test="$tag='pseq'">profileSequenceDescTag</xsl:when>
		<xsl:when test="$tag='rXYZ'">redMatrixColumnTag</xsl:when>
		<xsl:when test="$tag='rTRC'">redTRCTag</xsl:when>
		<xsl:when test="$tag='tech'">technologyTag</xsl:when>
		<xsl:when test="$tag='vued'">viewingCondDescTag</xsl:when>
		<xsl:when test="$tag='view'">viewingConditionsTag</xsl:when>
		<xsl:otherwise><xsl:value-of select="$tag"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="data">
		<h2>The Tagged Data</h2>
		<xsl:for-each select="*">
			<xsl:variable name="num"><xsl:value-of select="@name"/></xsl:variable>
			<xsl:variable name="tag"><xsl:value-of select="/ICCProfile/tagtable/entry[@name=$num]/tag"/></xsl:variable>
			<table border="1" width="100%" bgcolor="#FFCCCC">
			<caption>Tag block <xsl:value-of select="$num"/> [<xsl:value-of select="name()"/>]
			<xsl:value-of select="$tag"/>--
			<xsl:call-template name="get-meaning"><xsl:with-param name="tag"><xsl:value-of select="$tag"/></xsl:with-param></xsl:call-template>
			</caption>
			<tbody>
			<xsl:apply-templates select="*"/>
			</tbody>
			</table><br/>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="array">
		<tr>
		<td><xsl:value-of select="@name"/>[<xsl:value-of select="name()"/>]</td>
		<td>
		<table border="1">
		<xsl:apply-templates />
		</table>
		</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="dim">
		{
		<xsl:apply-templates />
		}
	</xsl:template>
	
	<xsl:template match="*">
		<tr>
		<td><xsl:value-of select="@name"/>[<xsl:value-of select="name()"/>]</td>
		<td><xsl:value-of select="."/></td>
		</tr>
	</xsl:template>

</xsl:stylesheet>
