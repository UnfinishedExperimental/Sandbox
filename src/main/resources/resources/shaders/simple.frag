#version 120
#pragma include includes/StdLib.frag
#pragma include includes/misc.h

in vec2 texcoord; 

uniform sampler2D data;

void main()
{
    vec2 pos = texcoord * 2. - 1.;
    FragColor = vec4(Unpack3PNFromFP32(texture(data, texcoord).x), 0);
}
