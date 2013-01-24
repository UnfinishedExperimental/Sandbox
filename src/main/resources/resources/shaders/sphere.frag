#version 120
#pragma include includes/StdLib.frag
#pragma include includes/gamma.h

void main()
{   
    FragColor = toGamma(vec4(1));
}
