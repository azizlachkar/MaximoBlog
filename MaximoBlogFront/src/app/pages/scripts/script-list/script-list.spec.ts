import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScriptList } from './script-list';

describe('ScriptList', () => {
  let component: ScriptList;
  let fixture: ComponentFixture<ScriptList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScriptList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScriptList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
