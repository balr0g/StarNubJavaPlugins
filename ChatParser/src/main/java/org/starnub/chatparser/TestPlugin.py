__author__ = 'Daniel'
from org.starnub.starbounddata.types.vectors.Vec2I import Vec2I


class Coord(Vec2I):
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def getX(self):
        return self.x

    def getY(self):
        return self.y
