#version 300 es
uniform mat4 uMVPMatrix; //�ܱ任����
uniform mat4 uMMatrix; //�任����
in vec3 aPosition;  //����λ��
out vec4 vPosition;//���ڴ��ݸ�ƬԪ��ɫ���ı���
void main() { 
   gl_Position = uMVPMatrix*vec4(aPosition,1); //�����ܱ任�������˴λ��ƴ˶���λ��  
   vPosition=uMMatrix*vec4(aPosition,1);
}