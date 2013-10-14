#version 130
#pragma include includes/StdLib.vert

out vec3 color;
out vec2 tex;

void main()
{
    int id = gl_VertexID;

    color = vec3(0);

vec2 pos2;
    if(id == 0){
        color.r = 1;
        pos2 = vec2(-1,-1);
    }else if(id == 1){
        color.g = 1;
        pos2 = vec2(1,-1);
    }else if(id == 2){
        color.b = 1;
        pos2 = vec2(-1,1);
    }else
        pos2 = vec2(1,1);

    gl_Position = vec4(pos2,0,1);
        

    tex = vec2((id *2) & 2, id & 2); 

    //gl_Position = vec4(tex * vec2(2,-2) + vec2(-1,1), 0, 1);
}